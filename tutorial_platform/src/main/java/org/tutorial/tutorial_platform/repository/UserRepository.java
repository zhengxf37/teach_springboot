package org.tutorial.tutorial_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorial.tutorial_platform.pojo.User;
import java.util.Optional;

/**
 * UserRepository - 用户数据访问接口
 *
 * 提供对用户(User)实体的数据库操作能力，包括：
 * - 基础CRUD操作：继承自JpaRepository的默认实现
 * - 自定义查询：根据业务需求扩展的查询方法
 *
 * 核心功能：
 * - 用户检索：通过用户名/邮箱查询用户信息
 * - 存在性验证：检查用户名/邮箱是否已被注册
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // 如果不存在则返回empty（不是NULL）
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
