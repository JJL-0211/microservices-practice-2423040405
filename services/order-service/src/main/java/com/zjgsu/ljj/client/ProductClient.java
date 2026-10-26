package com.zjgsu.ljj.client;

import com.zjgsu.ljj.config.ServiceProperties;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 调用 product-service：商品校验 + 扣库存。
 * 依赖服务不可用时返回明确错误，禁止跳过校验。
 */
@Component
public class ProductClient {

    private final RestTemplate rest;
    private final String baseUrl;

    public ProductClient(ServiceProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) props.getConnectTimeoutMs());
        factory.setReadTimeout((int) props.getReadTimeoutMs());
        this.rest = new RestTemplate(factory);
        this.baseUrl = props.getProductUrl();
    }

    /** 校验商品存在/上架/库存，并在 product-service 本地扣减库存。返回扣减后的商品快照。 */
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkAndDeduct(Long productId, int quantity) {
        String url = baseUrl + "/api/products/" + productId + "/check-and-deduct";
        try {
            return rest.postForObject(url, Map.of("quantity", quantity), Map.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("product-service 返回 " + e.getStatusCode()
                    + "：" + e.getStatusText());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("product-service 服务端错误 " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("product-service 不可用：" + e.getMessage());
        }
    }
}
