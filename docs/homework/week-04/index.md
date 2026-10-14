# 第 04 周作业记录：单体应用业务实现

本周在 `monolith/` 上实现个人选题的一条完整业务流程"商品下单流程"，包含模型、Service、REST API 与 MySQL 持久化。
基础包名 `com.zjgsu.ljj`，各层代码放在其子包 `model` / `repository` / `dto` / `service` / `controller` / `exception` 中。

---

## 一、业务建模

### 实现思路

选题为"Kitty 主题女性垂类内容带货电商系统"，本周落地其中一条核心业务闭环——**商品下单流程**：
浏览商品 → 查看详情 → 提交订单（业务规则校验）→ 生成订单并扣减库存 → 查询订单结果。

- 业务实体：商品 `Product`、订单 `ShopOrder`，关系为 **商品 1 : N 订单**。
- 业务规则：下单时商品必须存在且已上架，购买数量 1~5 件，库存充足；通过后按 `单价×数量` 计算金额并扣减库存，防止超卖。
- 详见 [docs/project-proposal.md](file:///d:/microservices-practice-2423040405/docs/project-proposal.md) 的"第 04 周"章节。

### 运行命令

无需运行，属设计说明。

### 结果

实体、关系（1:N）与 1 条 Service 业务规则已写入 `docs/project-proposal.md`。

---

## 二、单体应用实现

### 实现思路

按分层实现：Controller 只做 HTTP 输入输出，业务规则与库存扣减全部放在 Service，数据访问由 Repository 完成。

| 层 | 代码 |
| --- | --- |
| model | [Product.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/model/Product.java)、[ShopOrder.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/model/ShopOrder.java)、[OrderStatus.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/model/OrderStatus.java) |
| repository | [ProductRepository.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/repository/ProductRepository.java)、[ShopOrderRepository.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/repository/ShopOrderRepository.java) |
| service | [ProductService.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/service/ProductService.java)、[OrderService.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/service/OrderService.java) |
| controller | [ProductController.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/controller/ProductController.java)、[OrderController.java](file:///d:/microservices-practice-2423040405/monolith/src/main/java/com/zjgsu/ljj/controller/OrderController.java) |

REST 接口（≥4 个，含查询与创建，均以 `/api` 为前缀）：

| 方法 | 路径 | 说明 | 状态码 |
| --- | --- | --- | --- |
| GET | /api/products | 查询商品列表（`?onlyOnSale=true` 仅上架） | 200 |
| GET | /api/products/{id} | 查询商品详情 | 200 / 404 |
| POST | /api/orders | 下单 | 201 / 400 / 404 / 409 |
| GET | /api/orders/{id} | 查询订单详情 | 200 / 404 |
| GET | /api/orders | 查询订单列表 | 200 |

### 运行命令

```bash
cd monolith
./mvnw spring-boot:run
```

### 结果

接口可正常调用，创建返回 201、业务规则失败返回 400/404/409（见"测试与验证"截图）。
curl 示例见根目录 [README.md](file:///d:/microservices-practice-2423040405/README.md)。

---

## 三、数据库持久化

### 实现思路

- 接入本地 MySQL，库名 `kitty_shop`，连接配置见 [application.yaml](file:///d:/microservices-practice-2423040405/monolith/src/main/resources/application.yaml)。
- 使用 Spring Data JPA，数据访问集中在 `repository` 包。
- 使用 DTO 请求对象承载 API 输入：`CreateProductRequest`、`CreateOrderRequest`，响应使用 `ProductResponse`、`OrderResponse`。
- 表结构、字段与约束见 [docs/database.md](file:///d:/microservices-practice-2423040405/docs/database.md)。
- 可重复的数据准备：MySQL 演示数据 `src/main/resources/data.sql`（固定主键 + `ON DUPLICATE KEY UPDATE`）；测试数据由测试类通过 Repository 在 `@BeforeEach` 准备。

### 运行命令

```bash
# 1) 建库（本地 MySQL）
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS kitty_shop DEFAULT CHARACTER SET utf8mb4;"
# 2) 启动应用，自动建表并执行 data.sql
cd monolith && ./mvnw spring-boot:run
```

### 结果

应用启动后自动生成 `product`、`shop_order` 两张表，`data.sql` 写入 3 条演示商品；下单后 `shop_order` 新增记录且 `product.stock` 相应扣减。

---

## 四、测试与验证

### 实现思路

测试使用 H2 内存数据库（`src/test/resources/application.yaml`），无需 MySQL 即可重复运行，覆盖三层：

| 测试类 | 验证内容 |
| --- | --- |
| [RepositoryTest.java](file:///d:/microservices-practice-2423040405/monolith/src/test/java/com/zjgsu/ljj/repository/RepositoryTest.java) | Repository 读写：商品增改查、订单写入与按订单号/商品查询 |
| [OrderServiceTest.java](file:///d:/microservices-practice-2423040405/monolith/src/test/java/com/zjgsu/ljj/service/OrderServiceTest.java) | Service 业务规则：下单成功扣库存、库存不足 409、下架 409、超限购 400、商品不存在 404 |
| [OrderApiTest.java](file:///d:/microservices-practice-2423040405/monolith/src/test/java/com/zjgsu/ljj/controller/OrderApiTest.java) | API 路由与状态码：查询/创建 200、201，错误响应 404、409 |

### 运行命令

```bash
cd monolith
./mvnw test
```

### 结果

```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- OrderApiTest：6 个通过
- OrderServiceTest：5 个通过
- RepositoryTest：2 个通过
- MonolithApplicationTests：1 个通过

### 截图

见 `docs/homework/week-04/screenshots/`。