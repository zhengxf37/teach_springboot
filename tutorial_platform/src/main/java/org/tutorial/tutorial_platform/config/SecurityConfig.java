package org.tutorial.tutorial_platform.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SecurityConfig - 安全相关配置类
 *
 * 负责Spring Security相关组件的配置，包括：
 * - 密码编码器：配置BCrypt强哈希加密策略
 * - 安全过滤器：定义请求拦截规则（如需）
 * - 认证管理器：配置用户认证逻辑（如需）
 *
 * 核心功能：
 * - 密码加密策略：使用BCrypt算法加密密码
 * - Bean注册管理：向Spring容器注册安全相关组件
 * - 安全行为定制：可扩展其他安全配置项
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
@Configuration  // 标记这是Spring配置类
public class SecurityConfig {
    /**
     * 密码编码器Bean注册
     * 功能说明：
     * - 算法选择：采用BCrypt强哈希算法
     * - 强度配置：默认使用10次加密轮次
     * - 容器管理：由Spring统一管理单例实例,启动时会自动调用被@Bean标注的函数
     * @return PasswordEncoder BCrypt编码器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
