package com.zjgsu.ljj.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务异常：Service 层业务规则校验失败时抛出，携带对应的 HTTP 状态码。
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}