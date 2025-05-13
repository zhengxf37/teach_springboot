package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.tutorial.tutorial_platform.pojo.UserType;

/**
 * UserInfoUpdateDTO - 用户信息更新数据传输对象
 *
 * 用于接收前端提交的用户信息更新请求，包含：
 * - 用户ID
 * - 用户名
 * - 密码（加密后）
 * - 邮箱地址
 * - 用户类型（TEACHER/STUDENT）
 * - 创建时间（不可更改）
 *
 * 使用场景：
 * - 修改用户信息接口(/api/user/update)的请求体
 * - 通过@Valid注解触发自动参数校验(接收参数时)
 *
 * 示例请求：
 * {
 *   "userId": 123,
 *   "username": "new_teacher",
 *   "password": "SecurePass123",
 *   "email": "teacher@example.com",
 *   "userType": "TEACHER"
 * }
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
@Data
public class UserInfoUpdateDTO {

    /**
     * 用户唯一标识
     * @constraint 必须存在，且大于0
     */
    private Long userId;

    /**
     * 登录用户名
     * @constraint 非空且长度4-20字符
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    /**
     * 登录密码
     * @security 需加密传输并存储
     * @constraint 非空且长度6-32字符
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    /**
     * 电子邮箱
     * @constraint 非空且符合邮箱格式
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户角色类型
     * @see UserType 枚举定义
     * @constraint 必须指定角色类型
     */
    private UserType userType;
}
