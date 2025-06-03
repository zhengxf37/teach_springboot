package org.tutorial.tutorial_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tutorial.tutorial_platform.exception.TokenException;

import java.io.IOException;
import java.util.Objects;

/**
 * GlobalExceptionHandler - 全局异常处理器
 *
 * 统一处理Controller 层（或其调用的下层组件）抛出的异常，包括：
 * - Controller 方法本身抛出的异常
 * - Service 层或 Repository 层传递的异常
 * - 认证和授权相关的异常
 * - 文件操作相关的异常
 *
 * 核心功能：
 * - 统一异常处理，包括：
 *   - 业务异常
 *   - 参数异常
 *   - Token异常
 *   - 文件操作异常
 *
 * 元信息：
 * @author zxf
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

    /**
     * 处理Token相关异常
     * @param e Token异常
     * @return 错误响应
     */
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<String> handleTokenException(TokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // 处理文件操作异常
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("文件操作失败：" + e.getMessage());
    }

    // 处理参数非法异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    //字符串转换异常，匹配里的
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
