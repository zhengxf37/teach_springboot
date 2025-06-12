package org.tutorial.tutorial_platform.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/**
 * FileUploadService - 文件上传服务接口
 * 
 * 提供文件上传相关的核心业务逻辑，包括：
 * - 多文件上传：支持批量上传文件
 * - 文件列表查询：获取用户上传的文件列表
 * 
 * 安全说明：
 * - 所有操作都需要验证用户身份
 * - 用户只能访问自己的文件
 * 
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
public interface FileUploadService {
    /**
     * 上传多个文件
     * @param userId 用户ID
     * @param files 要上传的文件数组
     * @return 文件访问URL列表
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 当文件数组为空时抛出
     */
    List<String> uploadFiles(Long userId, MultipartFile[] files) throws IOException;

    /**
     * 获取用户上传的文件列表
     * @param userId 用户ID
     * @return 文件访问URL列表
     */
    List<String> listUserFiles(Long userId);
    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 当文件类型不正确时抛出
     */
    String uploadAvatar(Long userId, MultipartFile file) throws IOException;
    /**
     * 获取用户头像访问URL
     */
    String listUserAvatar(Long userId);
}
