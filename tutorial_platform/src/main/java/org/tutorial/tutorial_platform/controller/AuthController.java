package org.tutorial.tutorial_platform.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.service.AuthService;
import org.tutorial.tutorial_platform.vo.AuthResponseVO;
/**
 * AuthController - 统一认证控制器
 *
 * 处理所有与用户认证相关的HTTP请求，包括：
 * - 用户注册：创建新用户账号并返回认证信息
 * - 用户登录：验证凭证并返回认证令牌
 *
 * 元信息：
 * @author zxf
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 用户注册接口
     * @param registerDTO 接收注册请求参数，包含：
     *                    - username 用户名（4-20字符）
     *                    - password 密码（6-32字符）
     *                    - email 邮箱地址
     *                    - userType 用户类型（TEACHER/STUDENT）
     * @return 认证响应，包含：
     *         - userId 用户唯一标识
     *         - username 用户名
     *         - userType 用户类型
     * @throws MethodArgumentNotValidException 参数校验失败（错误码400）
     * @throws RuntimeException 用户名/邮箱已存在（错误码400）
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseVO> register(@Valid @RequestBody RegisterDTO registerDTO){
        return ResponseEntity.ok(authService.register(registerDTO));
    }

    /**
     * 用户登录接口
     * @param loginDTO 登录请求参数，包含：
     *                 - username 用户名
     *                 - password 密码
     * @return 认证响应，包含：
     *         - userId 用户唯一标识
     *         - username 用户名
     *         - userType 用户类型
     * @throws MethodArgumentNotValidException 参数校验失败（错误码400）
     * @throws RuntimeException 用户名/密码错误（错误码401）
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseVO> login(@Valid @RequestBody LoginDTO loginDTO){
        return ResponseEntity.ok(authService.login(loginDTO));
    }

}
