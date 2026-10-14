package com.zjgsu.ljj.dto;

import com.zjgsu.ljj.model.Product;

import java.math.BigDecimal;

/**
 * 商品响应体。
 */
public record ProductResponse(
        Long id,
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        Boolean onSale
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getOnSale()
        );
    }
}