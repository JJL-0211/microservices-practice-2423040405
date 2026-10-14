package com.zjgsu.ljj.service;

import com.zjgsu.ljj.dto.CreateOrderRequest;
import com.zjgsu.ljj.dto.OrderResponse;
import com.zjgsu.ljj.exception.BusinessException;
import com.zjgsu.ljj.exception.ResourceNotFoundException;
import com.zjgsu.ljj.model.OrderStatus;
import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.model.ShopOrder;
import com.zjgsu.ljj.repository.ProductRepository;
import com.zjgsu.ljj.repository.ShopOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单业务逻辑：实现“内容种草 → 下单”流程中的下单环节。
 *
 * <p>核心业务规则：下单时商品必须存在且处于上架状态，购买数量在 1~5 件之间，
 * 且库存充足；校验通过后按 单价 × 数量 计算订单金额并扣减库存，防止超卖。
 */
@Service
public class OrderService {

    /** 单笔订单限购上限（女性垂类美妆/服饰按件限购，防止恶意囤货） */
    public static final int MAX_QUANTITY_PER_ORDER = 5;

    private static final DateTimeFormatter ORDER_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ProductRepository productRepository;
    private final ShopOrderRepository shopOrderRepository;

    public OrderService(ProductRepository productRepository, ShopOrderRepository shopOrderRepository) {
        this.productRepository = productRepository;
        this.shopOrderRepository = shopOrderRepository;
    }

    /**
     * 下单：校验业务规则 → 扣减库存 → 生成订单。
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 规则 1：购买数量必须在 1~5 之间
        if (request.quantity() == null
                || request.quantity() < 1
                || request.quantity() > MAX_QUANTITY_PER_ORDER) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "单笔订单购买数量必须在 1-" + MAX_QUANTITY_PER_ORDER + " 件之间");
        }

        // 规则 2：商品必须存在
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("商品不存在: id=" + request.productId()));

        // 规则 3：商品必须已上架
        if (!Boolean.TRUE.equals(product.getOnSale())) {
            throw new BusinessException(HttpStatus.CONFLICT, "商品已下架，无法下单");
        }

        // 规则 4：库存必须充足，否则禁止下单，避免超卖
        if (product.getStock() < request.quantity()) {
            throw new BusinessException(HttpStatus.CONFLICT,
                    "库存不足，当前库存: " + product.getStock());
        }

        // 业务规则通过：扣减库存
        product.setStock(product.getStock() - request.quantity());

        BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));
        ShopOrder order = new ShopOrder(
                generateOrderNo(),
                product,
                request.quantity(),
                amount,
                OrderStatus.CREATED,
                LocalDateTime.now()
        );
        return OrderResponse.from(shopOrderRepository.save(order));
    }

    /** 按 ID 查询订单，不存在返回 404 */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        ShopOrder order = shopOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在: id=" + id));
        return OrderResponse.from(order);
    }

    /** 查询全部订单 */
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders() {
        return shopOrderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    private String generateOrderNo() {
        return "KO" + ORDER_NO_FORMATTER.format(LocalDateTime.now())
                + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
    }
}