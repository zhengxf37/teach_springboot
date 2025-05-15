package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.AuthService;
import org.tutorial.tutorial_platform.util.JwtUtil;
import org.tutorial.tutorial_platform.vo.AuthResponseVO;

/**
 * AuthServiceImp - 认证服务实现类
 *
 * 实现 AuthService 接口定义的核心认证逻辑，包括：
 * - 用户注册：处理新用户注册流程
 * - 用户登录：验证用户凭证有效性
 *
 * 依赖组件：
 * - UserRepository 用户数据访问接口
 * - PasswordEncoder 密码编码器
 * - JwtUtil JWT工具类
 *
 * 元信息：
 * @author zxf
 */
@Service
public class AuthServiceImp implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponseVO register(RegisterDTO registerDTO) {
        // 1. 唯一性校验
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 2. 实体构建
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setUserType(registerDTO.getUserType());

        // 3. 持久化操作
        User savedUser = userRepository.save(user);

        // 4. 生成token
        String token = jwtUtil.generateToken(savedUser);

        // 5. 返回认证响应
        return new AuthResponseVO(savedUser, token);
    }

    @Override
    public AuthResponseVO login(LoginDTO loginDTO) {
        // 1. 用户查询
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名错误"));

        // 2. 密码验证
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成token,在客户端中如果先注册后登录，新token会覆盖旧token
        String token = jwtUtil.generateToken(user);

        // 4. 返回认证响应
        return new AuthResponseVO(user, token);
    }
}

