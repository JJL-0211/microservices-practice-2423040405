package com.zjgsu.ljj.client;

import com.zjgsu.ljj.config.ServiceProperties;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * 调用 user-service：校验用户存在。
 */
@Component
public class UserClient {

    private final RestTemplate rest;
    private final String baseUrl;

    public UserClient(ServiceProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) props.getConnectTimeoutMs());
        factory.setReadTimeout((int) props.getReadTimeoutMs());
        this.rest = new RestTemplate(factory);
        this.baseUrl = props.getUserUrl();
    }

    public boolean exists(Long userId) {
        String url = baseUrl + "/api/users/" + userId;
        try {
            rest.getForObject(url, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("user-service 返回 " + e.getStatusCode() + "：" + e.getStatusText());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("user-service 不可用：" + e.getMessage());
        }
    }
}
