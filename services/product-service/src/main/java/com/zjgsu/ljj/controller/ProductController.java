package com.zjgsu.ljj.controller;

import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> list() { return service.list(); }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody CreateProductRequest req) {
        Product p = service.create(new Product(req.name(), req.category(),
                new BigDecimal(req.price()), req.stock(), req.onSale()));
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    /** 供 order-service 跨服务调用：校验 + 扣库存 */
    @PostMapping("/{id}/check-and-deduct")
    public Product checkAndDeduct(@PathVariable Long id,
                                   @RequestBody CheckDeductRequest req) {
        return service.checkAndDeduct(id, req.quantity());
    }

    public record CreateProductRequest(String name, String category,
                                       String price, Integer stock, Boolean onSale) {}
    public record CheckDeductRequest(int quantity) {}
}
