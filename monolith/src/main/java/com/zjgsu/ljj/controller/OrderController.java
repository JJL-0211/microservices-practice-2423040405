package com.zjgsu.ljj.controller;

import com.zjgsu.ljj.dto.CreateOrderRequest;
import com.zjgsu.ljj.dto.OrderResponse;
import com.zjgsu.ljj.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单 REST 接口。下单接口会触发 Service 中的业务规则校验。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 下单 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    /** 查询订单详情 */
    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    /** 查询订单列表 */
    @GetMapping
    public List<OrderResponse> list() {
        return orderService.listOrders();
    }
}