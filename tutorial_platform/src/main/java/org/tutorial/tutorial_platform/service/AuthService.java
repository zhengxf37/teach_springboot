package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.vo.AuthResponse;


/**
 * AuthService - 认证核心服务接口
 *
 * 提供用户认证相关的业务逻辑处理，包括：
 * - 用户注册：创建新用户账号并完成初始化
 * - 用户登录：验证用户凭证并生成认证信息
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-10
 */
public interface AuthService {
    AuthResponse register(RegisterDTO registerDTO);
    AuthResponse login(LoginDTO loginDTO);
}
