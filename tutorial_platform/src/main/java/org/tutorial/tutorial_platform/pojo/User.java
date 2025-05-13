package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

/**
 * User - 用户实体类
 *
 * 表示系统中的用户基础信息，包括：
 * - 用户唯一标识和账号信息
 * - 认证凭据（加密密码）
 * - 用户角色类型（教师/学生）
 * - 账号创建时间
 *
 *
 * 核心功能：
 * - 自动生成主键
 * - 密码字段加密存储
 * - 枚举类型持久化处理
 * - 自动记录创建时间
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
@Entity
@Table(name = "user")
@Data  // Lombok注解，自动生成getter/setter/toString等方法
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(unique = true, nullable = false, length = 50)  //最大长度50字符
    private String username;

    /**
     * 加密后的密码
     * @security 存储前必须经过BCrypt加密
     * &#064;constraint  不能为空，固定长度255字符（加密后）
     */
    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    /**
     * 用户角色类型
     * @see UserType 枚举定义
     * @mapping 使用字符串形式存储到数据库
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    /**
     * 账号创建时间
     * @auto 首次持久化时自动设置为当前时间
     * @constraint 不可更新
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private Date createTime;


}

