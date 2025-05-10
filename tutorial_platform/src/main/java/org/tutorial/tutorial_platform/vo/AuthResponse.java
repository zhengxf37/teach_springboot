package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.pojo.UserType;

/**
 * AuthResponse - 认证响应数据传输对象
 *
 * 封装用户认证成功后的响应数据，包含：
 * - 用户基础身份信息
 *
 * 使用场景：
 * - 用户登录成功响应
 *
 * 数据结构示例：
 * {
 *   "userId": 123,
 *   "username": "math_teacher",
 *   "email": "teacher@example.com",
 *   "userType": "TEACHER",
 * }
 *
 * 安全说明：
 * - 敏感字段(如email)可根据业务需求决定是否返回
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
@Data
public class AuthResponse {
    private Long userId;

    private String username;

    private String userType;

    public AuthResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.userType = String.valueOf(user.getUserType().name());
    }


}