package org.tutorial.tutorial_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 带有 @Configuration 注解的类会被解析，
 * 其中的 @Bean 方法会被调用，返回的对象会被注册为 Bean。
 */
@Configuration  // 标记这是Spring配置类
public class SecurityConfig {
    @Bean  //将返回值注册为Spring容器管理的Bean,启动时会自动调用被 @Bean 标注的函数 。
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 使用BCrypt强哈希算法加密密码
    }
}
