package com.zjgsu.ljj.dto;

import java.time.LocalDateTime;

/**
 * 统一错误响应体。
 */
public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
}