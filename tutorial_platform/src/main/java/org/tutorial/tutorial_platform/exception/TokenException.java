package org.tutorial.tutorial_platform.exception;

/**
 * TokenException - Token相关异常
 *
 * 用于封装所有与JWT token相关的异常，包括：
 * - token不存在
 * - token格式错误
 * - token已过期
 * - token验证失败
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }

}