package com.example.graduation_project.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * 全局异常捕获器
 */
@ControllerAdvice("com.example.graduation_project.controller")
public class CustomExceptionHandler {

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<Map<String, String>> handleCustomerException(CustomerException ex) {
        // 打印异常日志
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.error("CustomerException: ", ex);
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "服务器错误"));
    }
}