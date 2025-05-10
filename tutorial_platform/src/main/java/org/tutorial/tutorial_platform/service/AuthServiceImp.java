package org.tutorial.tutorial_platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.vo.AuthResponse;

@Service
public class AuthServiceImp implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public AuthResponse register(RegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setUserType(registerDTO.getUserType());
        User savedUser = userRepository.save(user);  // 返回增强后的user(添加主键id和创建时间）
        return new AuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名错误"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return new AuthResponse(user);
    }
}

