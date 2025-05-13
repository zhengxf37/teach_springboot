package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.pojo.UserType;

/**
 * UserInfoVO - 用户信息展示对象
 *
 * 用于封装返回给前端的用户信息，包含：
 * - 用户ID
 * - 用户名
 * - 邮箱地址
 * - 用户类型（TEACHER/STUDENT）
 * - 头像URL（可选）
 * - 注册时间
 *
 * 使用场景：
 * - 查询用户信息接口(/api/user/info)的返回体
 *
 * 示例响应：
 * {
 *   "userId": "123456",
 *   "username": "math_teacher",
 *   "email": "teacher@example.com",
 *   "userType": "TEACHER",
 *   "avatar": "https://example.com/avatar.jpg",
 *   "createTime": "2025-05-10T10:00:00Z"
 * }
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
@Data
public class UserInfoVO {
    /**
     * 用户唯一标识
     */
    private String userId;
    /**
     * 登录用户名
     */
    private String username;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 用户角色类型
     * @see UserType 枚举定义
     */
    private UserType userType;
    /**
     * 注册时间或创建时间
     */
    private String createTime;

    public UserInfoVO(User user) {
        this.userId = String.valueOf(user.getUserId());
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.userType = user.getUserType();
        this.createTime = user.getCreateTime().toString();

    }


}
