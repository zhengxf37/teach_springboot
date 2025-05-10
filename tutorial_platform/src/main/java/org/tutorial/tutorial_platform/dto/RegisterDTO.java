package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.tutorial.tutorial_platform.pojo.UserType;

/**
 * RegisterDTO - 用户注册数据传输对象
 *
 * 封装用户注册时前端提交的请求数据，包含：
 * - 账号基础信息（用户名/密码/邮箱）
 * - 用户角色类型（教师/学生）
 * 如果不符合注解的参数检验，则会抛出异常，需要在全局异常捕获器中捕获
 *
 * 使用场景：
 * - 用户注册接口(/api/auth/register)的请求体
 * - 通过@Valid注解触发自动参数校验(接收参数时)
 *
 * 示例请求：
 * {
 *   "username": "math_teacher",
 *   "password": "Secure123",
 *   "email": "teacher@example.com",
 *   "userType": "TEACHER"
 * }
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-010
 */
@Data
public class RegisterDTO {
    /**
     * 用户名
     * @constraint 非空且长度4-20个字符，否则抛出异常
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    /**
     * 登录密码
     * @security 需在前端加密后传输
     * @constraint 非空且长度6-32个字符，否则抛出异常
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    /**
     * 电子邮箱
     * @constraint 非空且符合邮箱格式，否则抛出异常
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户角色类型
     * @see UserType 枚举定义
     * @constraint 必须指定角色类型，否则抛出异常
     */
    @NotNull(message = "用户类型不能为空")
    private UserType userType;
}