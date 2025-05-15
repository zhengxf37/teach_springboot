package org.tutorial.tutorial_platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.service.UserInfoService;
import org.tutorial.tutorial_platform.vo.UserInfoVO;

import java.io.IOException;

/**
 * UserInfoController - 用户信息管理控制器
 * 
 * 提供以下功能：
 * - 查询用户个人信息
 * - 修改用户个人信息
 * - 上传用户头像
 * 
 * 安全说明：
 * - 所有接口都需要有效的JWT token
 * - 用户只能访问和修改自己的信息
 * 
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userService;

    /**
     * 查询用户个人信息接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @return 用户信息响应对象
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfoVO> getUserInfo(HttpServletRequest request) {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    /**
     * 修改用户个人信息接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @param userInfoUpdateDTO 用户信息更新对象
     * @return 用户信息响应对象
     */
    @PutMapping("/update")
    public ResponseEntity<UserInfoVO> updateUserInfo(
            HttpServletRequest request,
            @RequestBody UserInfoUpdateDTO userInfoUpdateDTO) {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        userInfoUpdateDTO.setUserId(userId);
        return ResponseEntity.ok(userService.updateUserInfo(userInfoUpdateDTO));
    }

    /**
     * 上传用户头像接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @param file 头像文件
     * @return 头像访问URL
     * @throws IOException 文件操作异常
     */
    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) throws IOException {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        // 调用service层处理头像上传
        String avatarUrl = userService.uploadAvatar(userId, file);
        return ResponseEntity.ok(avatarUrl);
    }
}
