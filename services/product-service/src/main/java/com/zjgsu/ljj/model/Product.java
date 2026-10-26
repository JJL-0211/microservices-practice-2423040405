package com.zjgsu.ljj.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "on_sale", nullable = false)
    private Boolean onSale = true;

    protected Product() {}

    public Product(String name, String category, BigDecimal price, Integer stock, Boolean onSale) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.onSale = onSale == null || onSale;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public Boolean getOnSale() { return onSale; }

    public void setStock(Integer stock) { this.stock = stock; }
}
