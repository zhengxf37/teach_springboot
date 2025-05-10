package org.tutorial.tutorial_platform.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginDTO - 用户登录数据传输对象
 *
 * 封装用户登录时前端提交的认证信息，包含：
 * - 登录用户名
 * - 登录密码
 *
 * 使用场景：
 * - 用户登录接口(/api/auth/login)的请求体
 * - 通过@Valid注解触发自动参数校验(接收参数时)
 * - 如果不符合注解的参数检验，则会抛出异常，需要在全局异常捕获器中捕获
 *
 * 示例请求：
 * {
 *   "username": "math_teacher",
 *   "password": "Secure123"
 * }
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
@Data
public class LoginDTO {
    /**
     * 登录用户名
     * @constraint 不能为空，否则抛出异常
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 登录密码
     * @security 需加密传输
     * @constraint 不能为空，否则抛出异常
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}