package org.tutorial.tutorial_platform.pojo;

/**
 * UserType - 用户角色枚举
 *
 * 定义系统支持的用户类型：
 * - TEACHER 教师账号
 * - STUDENT 学生账号
 *
 * 使用规范：
 * - 必须与@Enumerated(EnumType.STRING)配合使用
 * - 数据库存储值为枚举名称字符串
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
public enum UserType {
    TEACHER, STUDENT
}
