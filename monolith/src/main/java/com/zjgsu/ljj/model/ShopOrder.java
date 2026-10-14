package com.zjgsu.ljj.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体：一次下单行为的记录，关联所购商品。
 * 一个商品可被多个订单购买，因此订单与商品是多对一关系。
 */
@Entity
@Table(name = "shop_order",
        uniqueConstraints = @UniqueConstraint(name = "uk_order_no", columnNames = "order_no"))
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 业务订单号，全局唯一 */
    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    /** 关联商品 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_product"))
    private Product product;

    /** 购买数量 */
    @Column(nullable = false)
    private Integer quantity;

    /** 订单金额 = 商品单价 × 数量 */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** 订单状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrderStatus status;

    /** 下单时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ShopOrder() {
        // JPA 要求的无参构造
    }

    public ShopOrder(String orderNo, Product product, Integer quantity,
                     BigDecimal amount, OrderStatus status, LocalDateTime createdAt) {
        this.orderNo = orderNo;
        this.product = product;
        this.quantity = quantity;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}