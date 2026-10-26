package com.zjgsu.ljj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 跨服务调用配置：服务地址 + 超时，默认值可通过环境变量覆盖。
 *
 * <pre>
 * PRODUCT_SERVICE_URL=http://product-service:8080
 * USER_SERVICE_URL=http://user-service:8080
 * CONNECT_TIMEOUT_MS=2000
 * READ_TIMEOUT_MS=5000
 * </pre>
 */
@ConfigurationProperties(prefix = "app")
public class ServiceProperties {

    private String productUrl = "http://localhost:8081";
    private String userUrl = "http://localhost:8082";
    private long connectTimeoutMs = 2000;
    private long readTimeoutMs = 5000;

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getUserUrl() { return userUrl; }
    public void setUserUrl(String userUrl) { this.userUrl = userUrl; }

    public long getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(long connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public long getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(long readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }
}
