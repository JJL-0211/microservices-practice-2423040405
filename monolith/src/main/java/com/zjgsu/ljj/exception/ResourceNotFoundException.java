package com.zjgsu.ljj.exception;

import org.springframework.http.HttpStatus;

/**
 * 资源不存在异常，对应 HTTP 404。
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}