package com.zjgsu.ljj.repository;

import com.zjgsu.ljj.model.OrderStatus;
import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.model.ShopOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository 读写验证：确认 Spring Data JPA 对两张业务表能正常增删改查。
 */
@SpringBootTest
class RepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    @BeforeEach
    void cleanDatabase() {
        shopOrderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Repository：商品写入后可读回，库存可更新，并支持按上架状态查询")
    void productSaveReadUpdate() {
        Product saved = productRepository.save(
                new Product("Kitty 丝绒口红", "美妆", new BigDecimal("99.00"), 10, true));
        assertThat(saved.getId()).isNotNull();

        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Kitty 丝绒口红");

        found.get().setStock(6);
        productRepository.save(found.get());
        assertThat(productRepository.findById(saved.getId()).orElseThrow().getStock()).isEqualTo(6);

        assertThat(productRepository.findByOnSaleTrue()).hasSize(1);
    }

    @Test
    @DisplayName("Repository：订单写入后可读回，并支持按订单号与商品查询")
    void orderSaveRead() {
        Product product = productRepository.save(
                new Product("Kitty 联名连衣裙", "服饰", new BigDecimal("299.00"), 5, true));

        ShopOrder saved = shopOrderRepository.save(new ShopOrder(
                "KO-REPO-0001", product, 2, new BigDecimal("598.00"),
                OrderStatus.CREATED, LocalDateTime.now()));

        assertThat(saved.getId()).isNotNull();
        assertThat(shopOrderRepository.findByOrderNo("KO-REPO-0001")).isPresent();
        assertThat(shopOrderRepository.findByProductId(product.getId())).hasSize(1);
    }
}