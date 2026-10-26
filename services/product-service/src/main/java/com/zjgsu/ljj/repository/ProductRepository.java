package com.zjgsu.ljj.repository;

import com.zjgsu.ljj.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
