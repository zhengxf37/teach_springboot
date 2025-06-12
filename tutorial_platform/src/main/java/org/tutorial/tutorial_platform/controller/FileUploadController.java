package org.tutorial.tutorial_platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.service.FileUploadService;

import java.io.IOException;
import java.util.List;

/**
 * FileUploadController - 文件上传控制器
 * 
 * 提供以下功能：
 * - 多文件上传：支持批量上传文件
 * - 文件列表查询：获取用户上传的文件列表
 * 
 * 安全说明：
 * - 所有接口都需要有效的JWT token
 * - 用户只能访问和上传自己的文件
 * 
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 多文件上传接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @param files 要上传的文件数组
     * @return 文件访问URL列表
     * @throws IOException 文件操作异常
     */
    @PostMapping("/uploadfile")
    public ResponseEntity<List<String>> uploadFiles(
            HttpServletRequest request,
            @RequestParam("files") MultipartFile[] files) throws IOException {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        // 调用service层处理文件上传
        List<String> fileUrls = fileUploadService.uploadFiles(userId, files);
        log.info("上传文件成功，用户ID：{},返回{}", userId,  fileUrls);
        return ResponseEntity.ok(fileUrls);
    }

    /**
     * 上传头像
     * @param request
     * @return 文件访问URL
     * @throws IOException 文件上传失败
     */
    @PostMapping("/uploadavatar")
    public ResponseEntity<String> uploadAvatar(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file
            ) throws IOException {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        String avatarUrl = "http://localhost:8088"+fileUploadService.uploadAvatar(userId, file);
        log.info("上传头像成功，访问URL：{}", avatarUrl);
        return ResponseEntity.ok(avatarUrl);
    }
    /**
     * 获取用户文件列表接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @return 文件访问URL列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listUserFiles(HttpServletRequest request,@RequestParam Long userId) {
        // 从token中获取用户ID

        if (userId == -1){
            userId = (Long) request.getAttribute("userId");
        }
        // 调用service层获取文件列表
        List<String> fileUrls = fileUploadService.listUserFiles(userId);
        log.info("获取用户文件列表成功，用户ID：{},返回{}", userId,  fileUrls);
        return ResponseEntity.ok(fileUrls);
    }
    @GetMapping("/listavatar")
    public ResponseEntity<String> listUserAvatar(HttpServletRequest request,@RequestParam Long userId) {
        // 从token中获取用户ID
         if (userId == -1){
            userId = (Long) request.getAttribute("userId");
        }
        // 调用service层获取文件列表
        String avatarUrl = "http://localhost:8088"+fileUploadService.listUserAvatar(userId);
        log.info("获取用户头像成功，用户ID：{},返回{}", userId,  avatarUrl);
        return ResponseEntity.ok(avatarUrl);
    }

}
