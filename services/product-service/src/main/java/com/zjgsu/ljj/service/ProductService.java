package com.zjgsu.ljj.service;

import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> list() {
        return repo.findAll();
    }

    public Product get(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "商品不存在 id=" + id));
    }

    @Transactional
    public Product create(Product product) {
        return repo.save(product);
    }

    /**
     * 供 order-service HTTP 调用：原子地完成"校验 + 扣库存"。
     * 失败时抛出业务异常，由 controller 映射为 4xx 响应，order-service 据此不写本地数据。
     */
    @Transactional
    public Product checkAndDeduct(Long productId, int quantity) {
        Product p = repo.findById(productId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "商品不存在 id=" + productId));
        if (!Boolean.TRUE.equals(p.getOnSale())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "商品已下架，无法下单");
        }
        if (quantity < 1 || quantity > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "购买数量需在 1-5 之间");
        }
        if (p.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "库存不足，当前库存: " + p.getStock());
        }
        p.setStock(p.getStock() - quantity);
        return repo.save(p);
    }
}
