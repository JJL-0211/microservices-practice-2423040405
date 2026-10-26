package com.zjgsu.ljj.service;

import com.zjgsu.ljj.client.ProductClient;
import com.zjgsu.ljj.client.UserClient;
import com.zjgsu.ljj.model.OrderStatus;
import com.zjgsu.ljj.model.ShopOrder;
import com.zjgsu.ljj.repository.ShopOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单服务：下单前通过 HTTP 跨服务校验用户 / 商品并扣库存。
 * 任一 HTTP 调用失败都不得写入本地 shop_order。
 */
@Service
public class OrderService {

    private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ShopOrderRepository repo;
    private final ProductClient productClient;
    private final UserClient userClient;

    public OrderService(ShopOrderRepository repo, ProductClient productClient, UserClient userClient) {
        this.repo = repo;
        this.productClient = productClient;
        this.userClient = userClient;
    }

    @Transactional
    public ShopOrder create(Long userId, Long productId, int quantity) {
        if (quantity < 1 || quantity > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "数量需在 1-5 之间");
        }

        // 1) 校验用户
        if (!userClient.exists(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在 id=" + userId);
        }

        // 2) 校验 + 扣库存（若失败，product-service 抛 4xx/5xx，order-service 不写入）
        Map<?, ?> snapshot = productClient.checkAndDeduct(productId, quantity);
        Object priceObj = snapshot.get("price");
        BigDecimal price = new BigDecimal(String.valueOf(priceObj));

        // 3) 以上都成功后才写入本地 shop_order
        ShopOrder order = new ShopOrder(
                "KO" + NO_FMT.format(LocalDateTime.now()) + String.format("%03d", ThreadLocalRandom.current().nextInt(1000)),
                productId, userId, quantity,
                price.multiply(BigDecimal.valueOf(quantity)),
                OrderStatus.CREATED,
                LocalDateTime.now()
        );
        return repo.save(order);
    }

    public ShopOrder get(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在 id=" + id));
    }

    public List<ShopOrder> list() { return repo.findAll(); }
}
