package com.zjgsu.ljj.exception;

import com.zjgsu.ljj.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * 全局异常处理：把 Service 抛出的业务异常转换为统一的 JSON 错误响应与状态码。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务规则校验失败：使用异常自带的状态码（400 / 404 / 409） */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex) {
        return build(ex.getStatus(), ex.getMessage());
    }

    /** 请求体缺失或 JSON 无法解析统一返回 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "请求体格式错误: " + ex.getMostSpecificCause().getMessage());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        ApiError body = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }
}