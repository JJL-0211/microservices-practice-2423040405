package com.zjgsu.ljj.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体。order-service 独立拥有 shop_order 表。
 * 商品与用户信息在下单时通过 HTTP 从 product-service / user-service 获取，
 * 订单里只保存对方的 ID（productId / userId），不建立跨库外键。
 */
@Entity
@Table(name = "shop_order",
       uniqueConstraints = @UniqueConstraint(name = "uk_order_no", columnNames = "order_no"))
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    /** 关联 product-service 的 product.id */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 关联 user-service 的 user.id */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ShopOrder() {}

    public ShopOrder(String orderNo, Long productId, Long userId, Integer quantity,
                     BigDecimal amount, OrderStatus status, LocalDateTime createdAt) {
        this.orderNo = orderNo;
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public Long getProductId() { return productId; }
    public Long getUserId() { return userId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getAmount() { return amount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
