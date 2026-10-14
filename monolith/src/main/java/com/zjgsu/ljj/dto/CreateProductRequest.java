package com.zjgsu.ljj.dto;

import java.math.BigDecimal;

/**
 * 创建商品请求体。
 */
public record CreateProductRequest(
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        Boolean onSale
) {
}