package com.zjgsu.ljj.controller;

import com.zjgsu.ljj.dto.CreateProductRequest;
import com.zjgsu.ljj.dto.ProductResponse;
import com.zjgsu.ljj.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品 REST 接口。Controller 只负责 HTTP 输入输出，不包含数据访问逻辑。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 查询商品列表，onlyOnSale=true 时只返回上架商品 */
    @GetMapping
    public List<ProductResponse> list(@RequestParam(defaultValue = "false") boolean onlyOnSale) {
        return productService.list(onlyOnSale);
    }

    /** 查询商品详情 */
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }

    /** 创建商品 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody CreateProductRequest request) {
        return productService.create(request);
    }
}