package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.FileUploadService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FileUploadServiceImp - 文件上传服务实现类
 * 
 * 实现文件上传相关的核心业务逻辑，包括：
 * - 多文件上传：支持批量上传文件
 * - 文件列表查询：获取用户上传的文件列表
 * 
 * 核心功能：
 * - 文件存储：将上传的文件保存到指定目录
 * - 文件命名：生成唯一的文件名
 * - 目录管理：自动创建用户专属目录
 * - 文件查询：获取用户上传的文件列表
 * 
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
@Service
public class FileUploadServiceImp implements FileUploadService {
    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 上传多个文件
     * @param userId 用户ID
     * @param files 要上传的文件数组
     * @return 文件访问URL列表
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 当文件数组为空时抛出
     */
    @Override
    public List<String> uploadFiles(Long userId, MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("未提供要上传的文件");
        }

        List<String> fileUrls = new ArrayList<>();
        
        // 1. 构建用户专属目录
        File userDir = new File(uploadDir + File.separator + userId);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        // 2. 处理每个文件
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // 2.1 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            // 2.2 保存文件
            File dest = new File(userDir.getAbsolutePath() + File.separator + uniqueFilename);
            file.transferTo(dest);

            // 2.3 生成访问URL
            String fileUrl = "/uploads/" + userId + "/" + uniqueFilename;
            fileUrls.add(fileUrl);
        }

        return fileUrls;
    }

    /**
     * 获取用户上传的文件列表
     * @param userId 用户ID
     * @return 文件访问URL列表
     */
    @Override
    public List<String> listUserFiles(Long userId) {
        List<String> fileUrls = new ArrayList<>();

        // 1. 构建用户目录路径
        File userDir = new File(uploadDir + File.separator + userId);
        if (!userDir.exists()) {
            return fileUrls; // 目录不存在，返回空列表
        }

        // 2. 遍历目录下的所有文件
        File[] files = userDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String fileUrl = "/uploads/" + userId + "/" + fileName;
                fileUrls.add(fileUrl);
            }
        }

        return fileUrls;
    }

    /**
     * 上传头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return
     * @throws IOException
     */
    @Override
    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        // 1. 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 验证文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 3. 检查文件类型是否为图片
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                !(originalFilename.endsWith(".jpg") ||
                originalFilename.endsWith(".jpeg") ||
                originalFilename.endsWith(".png"))) {
            throw new IllegalArgumentException("仅支持 jpg/png/jpeg 格式的图片");
        }

        // 4. 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 5. 生成唯一文件名
        String filename = "avatar_" + userId + extension;

        // 6. 构建头像目录路径
        File avatarDir = new File(uploadDir + "/avatars");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }

        // 7. 保存文件
        File dest = new File(avatarDir.getAbsolutePath() + File.separator + filename);
        file.transferTo(dest);

        // 8. 返回访问路径
        return "/uploads/avatars/" + filename;

    }

} 