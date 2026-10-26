# Kitty 蜜选电商系统：微服务拆分设计

> 保留仓库中的 `monolith/` 作为单体版本；在仓库根目录新建 `services/`，每个
> `services/<服务名>/` 都是独立 Spring Boot 工程，拥有独立 `pom.xml` 与 `src/`。

## 拆分原则

- **高内聚、低耦合**：每个服务只拥有一组强内聚的数据与业务。
- **每个服务只读写自己的数据**，通过 HTTP API 获取其他服务的信息。
- **跨服务关联保存对方的 ID**，不建立跨库外键，也不直接调用对方的 Repository。
- **跨服务调用用 `RestTemplate`**，显式配置连接超时与读取超时，依赖不可用时返回明确错误。

## 单体 → 微服务映射

| 单体模块 | 归属服务 | 说明 |
| --- | --- | --- |
| `model.Product`、`ProductRepository`、商品 CRUD 与库存管理 | `product-service` | 商品主数据与库存 |
| **新增** 用户实体与用户 CRUD | `user-service` | 电商下单方，原单体无此模块 |
| `model.ShopOrder`、`ShopOrderRepository`、下单业务 | `order-service` | 下单时通过 HTTP 调用 product-service / user-service |

## 三个微服务

| 服务 | 端口 | 数据库 | 拥有的表 | 核心职责 |
| --- | --- | --- | --- | --- |
| `product-service` | 8081 | `kitty_product` | `product` | 商品 CRUD、按 ID 查询、库存扣减 |
| `user-service` | 8082 | `kitty_user` | `user` | 用户 CRUD、按 ID 查询 |
| `order-service` | 8083 | `kitty_order` | `shop_order` | 下单（调用 product-service 校验/扣库存 + 调用 user-service 校验用户）、查询订单 |

每个服务独立使用自己的数据库与账号（Compose 中共用同一个 MySQL 8.4.11 实例，但各建独立库），JPA `ddl-auto=update` 自动建表。

## 服务间调用关系

```
┌───────────────┐   HTTP   ┌─────────────────┐
│  order-service │ ──────► │  product-service │
│  (下单主服务)   │         │  (校验商品/扣库存)│
└───────┬───────┘         └─────────────────┘
        │
        │ HTTP
        ▼
┌───────────────┐
│  user-service  │
│  (校验用户)     │
└───────────────┘
```

跨服务业务流程（下单）：

1. `order-service` 收到 `POST /api/orders`，拿到 `userId` + `productId` + `quantity`；
2. 调用 `user-service GET /api/users/{id}` 校验用户存在，不存在返回 **400**；
3. 调用 `product-service POST /api/products/{id}/check-and-deduct`，让商品服务自己完成
   "商品存在 / 已上架 / 库存充足" 校验 + 扣减库存，并返回扣减后的商品快照；
4. 仅当上述两个调用都成功时，`order-service` 才在**自己的数据库**写入 `shop_order`；
5. 若任一依赖调用返回 4xx/5xx 或超时，`order-service` **不写入本地数据**，直接返回明确错误。

> 本周只验证基本成功与失败场景，不要求跨服务原子事务、自动补偿或消息机制。
> 仍会出现"远端成功、本地失败"窗口：例如步骤 3 扣减了 product-service 的库存，
> 但 order-service 本地写入因 MySQL 偶发错误失败，此时 product-service 库存已扣、
> 订单却未产生——这是两阶段以外的天然窗口，后续可通过消息队列 / Seata 解决。

## Mermaid 架构图

```mermaid
graph TD
    subgraph Clients
        C1[浏览器 / curl]
    end

    subgraph Services
        PS[product-service<br/>:8081<br/>DB: kitty_product<br/>表: product]
        US[user-service<br/>:8082<br/>DB: kitty_user<br/>表: user]
        OS[order-service<br/>:8083<br/>DB: kitty_order<br/>表: shop_order]
    end

    subgraph MySQL 8.4.11 (同一实例，3 个独立库)
        PDB[kitty_product.product]
        UDB[kitty_user.user]
        ODB[kitty_order.shop_order]
    end

    C1 --> PS
    C1 --> US
    C1 --> OS

    OS -- "HTTP 校验用户" --> US
    OS -- "HTTP 校验+扣库存" --> PS

    PS -- JPA --> PDB
    US -- JPA --> UDB
    OS -- JPA --> ODB
```

## 代码结构

```text
services/
├── product-service/
│   ├── pom.xml
│   └── src/main/java/com/zjgsu/ljj/
│       ├── ProductServiceApplication.java
│       ├── controller/ProductController.java
│       ├── service/ProductService.java
│       ├── repository/ProductRepository.java
│       ├── model/Product.java                 (原单体 Product 迁来)
│       └── dto/*.java
├── user-service/
│   ├── pom.xml
│   └── src/main/java/com/zjgsu/ljj/
│       ├── UserServiceApplication.java
│       ├── controller/UserController.java
│       ├── service/UserService.java
│       ├── repository/UserRepository.java
│       ├── model/User.java                    (新建)
│       └── dto/*.java
└── order-service/
    ├── pom.xml
    └── src/main/java/com/zjgsu/ljj/
        ├── OrderServiceApplication.java
        ├── controller/OrderController.java
        ├── service/OrderService.java
        ├── client/ProductClient.java           (RestTemplate 调 product-service)
        ├── client/UserClient.java              (RestTemplate 调 user-service)
        ├── repository/ShopOrderRepository.java
        ├── model/ShopOrder.java                (原单体 ShopOrder 迁来)
        └── dto/*.java

compose.yaml                    # 仓库根目录，一键启动 mysql + 3 个服务
scripts/
├── mysql-init.sql              # 创建 3 个业务库
└── smoke-test.sh               # 根目录可执行，健康检查 + 下单成功/失败验证
```

## 依赖资源不可用时的失败处理

- `RestTemplate` 配置 `connectTimeout=2s`、`readTimeout=5s`；
- 捕获 `ResourceAccessException`（超时 / 连接拒绝）和 `HttpClientErrorException`（4xx）、
  `HttpServerErrorException`（5xx）；
- 失败时 **不写入本地** `shop_order`，直接返回明确错误（例如
  `{"status":503,"message":"product-service 不可用"}`）。
