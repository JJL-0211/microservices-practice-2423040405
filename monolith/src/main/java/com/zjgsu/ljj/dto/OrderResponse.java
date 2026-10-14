package com.zjgsu.ljj.dto;

import com.zjgsu.ljj.model.ShopOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应体：把订单及其关联商品的关键信息扁平化返回，避免暴露 JPA 实体。
 */
public record OrderResponse(
        Long id,
        String orderNo,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {

    public static OrderResponse from(ShopOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}