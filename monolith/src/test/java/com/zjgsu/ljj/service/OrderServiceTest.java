package com.zjgsu.ljj.service;

import com.zjgsu.ljj.dto.CreateOrderRequest;
import com.zjgsu.ljj.dto.OrderResponse;
import com.zjgsu.ljj.exception.BusinessException;
import com.zjgsu.ljj.exception.ResourceNotFoundException;
import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.repository.ProductRepository;
import com.zjgsu.ljj.repository.ShopOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Service 业务规则验证：下单时的上架校验、限购校验与库存校验。
 */
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    @BeforeEach
    void cleanDatabase() {
        shopOrderRepository.deleteAll();
        productRepository.deleteAll();
    }

    private Product persistProduct(int stock, boolean onSale) {
        return productRepository.save(
                new Product("Kitty 测试商品", "美妆", new BigDecimal("100.00"), stock, onSale));
    }

    @Test
    @DisplayName("业务规则：库存充足时下单成功，扣减库存并按单价×数量计算金额")
    void createOrderSuccess() {
        Product product = persistProduct(5, true);

        OrderResponse response = orderService.createOrder(new CreateOrderRequest(product.getId(), 2));

        assertThat(response.orderNo()).startsWith("KO");
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.status()).isEqualTo("CREATED");
        assertThat(response.amount()).isEqualByComparingTo("200.00");
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(3);
        assertThat(shopOrderRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("业务规则：库存不足时下单失败(409)，且不扣减库存、不生成订单")
    void createOrderInsufficientStock() {
        Product product = persistProduct(1, true);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> orderService.createOrder(new CreateOrderRequest(product.getId(), 2)))
                .withMessageContaining("库存不足")
                .satisfies(ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(1);
        assertThat(shopOrderRepository.count()).isZero();
    }

    @Test
    @DisplayName("业务规则：商品已下架时下单失败(409)")
    void createOrderOffShelf() {
        Product product = persistProduct(10, false);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> orderService.createOrder(new CreateOrderRequest(product.getId(), 1)))
                .withMessageContaining("下架")
                .satisfies(ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    @DisplayName("业务规则：单笔订单数量超过限购上限时下单失败(400)")
    void createOrderOverPurchaseLimit() {
        Product product = persistProduct(10, true);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> orderService.createOrder(
                        new CreateOrderRequest(product.getId(), OrderService.MAX_QUANTITY_PER_ORDER + 1)))
                .withMessageContaining("购买数量")
                .satisfies(ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("业务规则：商品不存在时下单失败(404)")
    void createOrderProductNotFound() {
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> orderService.createOrder(new CreateOrderRequest(999999L, 1)))
                .satisfies(ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}