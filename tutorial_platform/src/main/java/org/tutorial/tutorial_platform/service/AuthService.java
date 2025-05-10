package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.vo.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterDTO registerDTO);
    AuthResponse login(LoginDTO loginDTO);
}
