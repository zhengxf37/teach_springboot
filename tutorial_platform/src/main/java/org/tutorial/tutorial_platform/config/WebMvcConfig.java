package org.tutorial.tutorial_platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tutorial.tutorial_platform.util.JwtInterceptor;

/**
 * WebMvcConfig - Web MVC配置类
 *
 * 配置Spring MVC相关组件，包括：
 * - 拦截器注册：配置JWT认证拦截器
 * ......
 * 核心功能：
 * - 拦截器管理：注册和管理请求拦截器
 * - 请求处理：配置请求处理相关的组件
 * 注：Sprint MVC会先拦截HTTP请求，在这里注册了拦截器以后，将会对请求进行token验证以及解析。
 * 元信息：
 * @author zxf
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)  // 注册自定义拦截器实例
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(        // 排除不需要认证的路径
                        "/api/auth/login",   // 登录接口
                        "/api/auth/register" // 注册接口
                );
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径生效
                .allowedOrigins("http://localhost:8080") // 允许的前端域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true); // 是否允许发送 Cookie
    }
} 