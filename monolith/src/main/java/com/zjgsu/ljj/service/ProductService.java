package com.zjgsu.ljj.service;

import com.zjgsu.ljj.dto.CreateProductRequest;
import com.zjgsu.ljj.dto.ProductResponse;
import com.zjgsu.ljj.exception.BusinessException;
import com.zjgsu.ljj.exception.ResourceNotFoundException;
import com.zjgsu.ljj.model.Product;
import com.zjgsu.ljj.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品业务逻辑：商品查询与商品创建。
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** 查询商品列表，onlyOnSale 为 true 时只返回上架商品 */
    @Transactional(readOnly = true)
    public List<ProductResponse> list(boolean onlyOnSale) {
        List<Product> products = onlyOnSale
                ? productRepository.findByOnSaleTrue()
                : productRepository.findAll();
        return products.stream().map(ProductResponse::from).toList();
    }

    /** 按 ID 查询商品，不存在返回 404 */
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商品不存在: id=" + id));
        return ProductResponse.from(product);
    }

    /** 创建商品 */
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "商品名称不能为空");
        }
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "商品价格必须大于 0");
        }
        if (request.stock() == null || request.stock() < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "库存不能为负数");
        }
        Product product = new Product(
                request.name(),
                request.category() == null ? "未分类" : request.category(),
                request.price(),
                request.stock(),
                request.onSale()
        );
        return ProductResponse.from(productRepository.save(product));
    }
}