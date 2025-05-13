package org.tutorial.tutorial_platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.service.UserInfoService;
import org.tutorial.tutorial_platform.vo.UserInfoVO;

import java.io.File;
import java.util.UUID;

/**
 * UserInfoController - 用户信息管理控制器
 * 提供以下功能：
 * - 查询用户个人信息
 * - 修改用户个人信息
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
@RestController
@RequestMapping("/api/user")//暂定接口？
public class UserInfoController {

    @Autowired
    private UserInfoService userService;
    @Value("${file.upload-dir}")
    private String uploadDir; // 接收 application.properties 配置的路径

    /**
     * 查询用户个人信息接口
     * @param  userId 用户唯一标识
     * @return 用户信息响应对象
     *
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfoVO> getUserInfo(Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    /**
     * 修改用户个人信息接口
     * @param userId 用户ID（来自URL）
     * @param userInfoUpdateDTO 用户信息更新对象
     * @return 用户信息响应对象
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserInfoVO> updateUserInfo(
            @PathVariable Long userId,
            @RequestBody UserInfoUpdateDTO userInfoUpdateDTO) {
        userInfoUpdateDTO.setUserId(userId);
        return ResponseEntity.ok(userService.updateUserInfo(userInfoUpdateDTO));
    }
}
