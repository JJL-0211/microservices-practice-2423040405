package com.zjgsu.ljj.dto;

/**
 * 下单请求体：商品 ID + 购买数量。
 */
public record CreateOrderRequest(
        Long productId,
        Integer quantity
) {
}