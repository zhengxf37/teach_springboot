package org.tutorial.tutorial_platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * GlobalExceptionHandler - 全局异常处理器
 *
 * 统一处理Controller 层（或其调用的下层组件）抛出的异常抛出的异常，包括：
 * - Controller 方法本身抛出的异常
 * - Service 层或 Repository 层传递的异常
 *
 * 核心功能：
 * - 统一异常处理
 *
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-5-9
 */
@RestControllerAdvice  // 标记该类为全局异常处理组件
public class GlobalExceptionHandler {
    // 处理业务异常
    @ExceptionHandler(RuntimeException.class)  // 指定该方法处理RuntimeException类型的异常（包括子类）
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // 处理参数异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        // 1. 从异常中提取第一个字段错误信息
        String errorMsg = Objects.requireNonNull(e.getBindingResult()  // 返回一个BindingResult对象，这个对象里存储了所有的校验错误信息。
                        .getFieldError()) // 获取第一个字段错误
                        .getDefaultMessage(); // 获取校验注解中的message设置的值，如"用户名错误"

        // 2. 构建ResponseEntity响应
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // HTTP 400状态码
                .body(errorMsg); // 响应体为校验失败信息
    }
}
