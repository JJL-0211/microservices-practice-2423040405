package com.zjgsu.ljj.controller;

import com.zjgsu.ljj.model.ShopOrder;
import com.zjgsu.ljj.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req) {
        try {
            ShopOrder order = service.create(req.userId(), req.productId(), req.quantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
        } catch (ResponseStatusException e) {
            // 业务校验失败（用户不存在、库存不足等），返回明确错误
            Map<String, Object> err = new HashMap<>();
            err.put("status", e.getStatusCode().value());
            err.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(err);
        } catch (RuntimeException e) {
            // 跨服务调用失败（依赖服务不可用、超时等）
            Map<String, Object> err = new HashMap<>();
            err.put("status", 503);
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        ShopOrder o;
        try { o = service.get(id); }
        catch (ResponseStatusException e) { return ResponseEntity.status(e.getStatusCode()).body(Map.of("status", e.getStatusCode().value(), "message", e.getReason())); }
        return ResponseEntity.ok(toResponse(o));
    }

    @GetMapping
    public List<ShopOrder> list() { return service.list(); }

    private Map<String, Object> toResponse(ShopOrder o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("orderNo", o.getOrderNo());
        m.put("productId", o.getProductId());
        m.put("userId", o.getUserId());
        m.put("quantity", o.getQuantity());
        m.put("amount", o.getAmount());
        m.put("status", o.getStatus());
        m.put("createdAt", o.getCreatedAt());
        return m;
    }

    public record CreateOrderRequest(Long userId, Long productId, int quantity) {}
}
