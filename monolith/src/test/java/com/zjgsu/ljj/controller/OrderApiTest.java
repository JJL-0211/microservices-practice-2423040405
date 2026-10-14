package com.zjgsu.ljj.controller;

import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.repository.ProductRepository;
import com.zjgsu.ljj.repository.ShopOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API 验证：REST 接口的路由、请求响应与状态码（含创建、查询与错误响应）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    private Long productId;

    @BeforeEach
    void setUp() {
        shopOrderRepository.deleteAll();
        productRepository.deleteAll();
        Product product = productRepository.save(
                new Product("Kitty 香水", "美妆", new BigDecimal("199.00"), 3, true));
        productId = product.getId();
    }

    @Test
    @DisplayName("API：查询商品列表返回 200")
    void listProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stock").value(3));
    }

    @Test
    @DisplayName("API：创建商品返回 201")
    void createProduct() throws Exception {
        String body = "{\"name\":\"Kitty 口红\",\"category\":\"美妆\",\"price\":88.00,\"stock\":10,\"onSale\":true}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.stock").value(10));

        assertThat(productRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("API：下单成功返回 201，并扣减库存")
    void createOrderSuccess() throws Exception {
        String body = "{\"productId\":" + productId + ",\"quantity\":2}";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNo").exists())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.amount").isNumber());

        assertThat(productRepository.findById(productId).orElseThrow().getStock()).isEqualTo(1);
        assertThat(shopOrderRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("API：库存不足下单返回 409")
    void createOrderInsufficientStock() throws Exception {
        String body = "{\"productId\":" + productId + ",\"quantity\":5}";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        assertThat(productRepository.findById(productId).orElseThrow().getStock()).isEqualTo(3);
    }

    @Test
    @DisplayName("API：商品不存在下单返回 404")
    void createOrderProductNotFound() throws Exception {
        String body = "{\"productId\":999999,\"quantity\":1}";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("API：按 ID 查询订单返回 200")
    void getOrderById() throws Exception {
        String body = "{\"productId\":" + productId + ",\"quantity\":1}";
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        Long orderId = shopOrderRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.quantity").value(1));
    }
}