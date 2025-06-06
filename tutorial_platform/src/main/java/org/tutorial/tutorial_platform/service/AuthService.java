package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.vo.AuthResponseVO;


/**
 * AuthService - 认证核心服务接口
 *
 * 提供用户认证相关的业务逻辑处理，包括：
 * - 用户注册：创建新用户账号并完成初始化
 * - 用户登录：验证用户凭证并生成认证信息
 *
 * 元信息：
 * @author zxf
 */
public interface AuthService {
    AuthResponseVO register(RegisterDTO registerDTO);
    AuthResponseVO login(LoginDTO loginDTO);
}
