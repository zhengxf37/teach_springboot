package org.tutorial.tutorial_platform.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.tutorial.tutorial_platform.exception.TokenException;

/**
 * JwtInterceptor - JWT认证拦截器
 * 在请求到达 Controller 之前对 JWT 令牌进行统一验证和用户信息提取，包括：
 * - 检查token是否存在
 * - 验证token的签名和过期时间
 * - 提取用户信息并设置到请求属性中
 *
 * 核心功能：
 * - 请求拦截：拦截需要认证的API请求
 * - token验证：验证token的有效性
 * - 用户信息传递：将用户信息传递给后续处理
 *
 * 元信息：
 * @author zxf
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 传入参数包括
     * request:封装了HTTP请求的所有信息
     * response:封装了对HTTP响应的控制
     * handle:表示最终处理该请求的处理器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 0. 直接放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 获取Header中名为token的字段值
        String token = request.getHeader("Token");
        if (token == null) {
            throw new TokenException("未提供有效的认证令牌!");  // 合法形式是"token"
        }

        // 2. 提取并验证Token
        String username = jwtUtil.extractUsername(token);
        if (!jwtUtil.validateToken(token, username)) {
            throw new TokenException("认证令牌已失效!"); // 直接抛出
        }

        // 3. 存储用户信息到当前http请求request中，以便controller层可以直接使用
        request.setAttribute("username", username);
        request.setAttribute("userId", jwtUtil.extractClaim(token, claims -> claims.get("userId", Long.class)));
        request.setAttribute("userType", jwtUtil.extractClaim(token, claims -> claims.get("userType", String.class)));

        return true;
    }

} 