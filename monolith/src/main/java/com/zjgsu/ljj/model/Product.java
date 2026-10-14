package com.zjgsu.ljj.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * 商品实体：内容带货流程中被种草、可下单的商品。
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 商品名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 商品分类：美妆 / 服饰 / 母婴 */
    @Column(nullable = false, length = 20)
    private String category;

    /** 商品单价 */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** 可售库存 */
    @Column(nullable = false)
    private Integer stock;

    /** 是否上架：下架商品不可下单 */
    @Column(name = "on_sale", nullable = false)
    private Boolean onSale = true;

    protected Product() {
        // JPA 要求的无参构造
    }

    public Product(String name, String category, BigDecimal price, Integer stock, Boolean onSale) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.onSale = onSale == null || onSale;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }
}