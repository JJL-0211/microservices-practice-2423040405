package com.zjgsu.ljj.repository;

import com.zjgsu.ljj.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 商品数据访问接口，由 Spring Data JPA 自动实现。
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** 查询全部上架商品 */
    List<Product> findByOnSaleTrue();

    /** 按分类查询上架商品 */
    List<Product> findByCategoryAndOnSaleTrue(String category);
}