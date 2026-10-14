package com.zjgsu.ljj.repository;

import com.zjgsu.ljj.model.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口，由 Spring Data JPA 自动实现。
 */
public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

    /** 按订单号查询 */
    Optional<ShopOrder> findByOrderNo(String orderNo);

    /** 按商品查询订单 */
    List<ShopOrder> findByProductId(Long productId);
}