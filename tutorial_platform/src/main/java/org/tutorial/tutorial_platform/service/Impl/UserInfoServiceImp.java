package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.UserInfoService;
import org.tutorial.tutorial_platform.vo.UserInfoVO;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * UserInfoServiceImp - 用户信息服务实现类
 * 
 * 实现用户信息相关的核心业务逻辑，包括：
 * - 查询用户信息：根据用户ID获取用户详细信息
 * - 更新用户信息：修改用户的基本信息
 * - 头像管理：处理用户头像的上传和存储
 * 
 * 核心功能：
 * - 用户信息查询：支持通过ID查询用户详细信息
 * - 信息更新：支持修改用户名、邮箱等基本信息
 * - 安全验证：更新信息时进行密码验证
 * - 唯一性检查：确保用户名和邮箱的唯一性
 * - 文件处理：支持头像文件的上传和存储
 * 
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
@Service
public class UserInfoServiceImp implements UserInfoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 获取用户详细信息
     * @param userId 用户ID
     * @return 用户信息视图对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    @Override
    public UserInfoVO getUserInfo(Long userId) {
        // 1. 查询用户实体
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 构建并返回视图对象
        return new UserInfoVO(user);
    }

    /**
     * 更新用户信息
     * @param userInfoUpdateDTO 用户信息更新对象
     * @return 更新后的用户信息视图对象
     * @throws RuntimeException 当以下情况发生时抛出：
     *                         - 用户不存在
     *                         - 密码验证失败
     *                         - 用户名/邮箱已被使用
     */
    @Override
    public UserInfoVO updateUserInfo(UserInfoUpdateDTO userInfoUpdateDTO) {
        // 1. 查询用户实体
        User user = userRepository.findById(userInfoUpdateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 验证密码
        if (!passwordEncoder.matches(userInfoUpdateDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 检查用户名唯一性
        if (!user.getUsername().equals(userInfoUpdateDTO.getUsername()) &&
            userRepository.existsByUsername(userInfoUpdateDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 4. 检查邮箱唯一性
        if (!user.getEmail().equals(userInfoUpdateDTO.getEmail()) &&
            userRepository.existsByEmail(userInfoUpdateDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 5. 更新用户信息
        user.setUsername(userInfoUpdateDTO.getUsername());
        user.setEmail(userInfoUpdateDTO.getEmail());
        user.setUserType(userInfoUpdateDTO.getUserType());

        // 6. 保存更新
        User updatedUser = userRepository.save(user);
        return new UserInfoVO(updatedUser);
    }

    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像访问URL
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 当文件类型不正确时抛出
     */
    @Override
    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        // 1. 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }

        // 3. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = "avatar_" + userId + "_" + UUID.randomUUID() + extension;

        // 4. 确保目录存在
        File uploadDirFile = new File(uploadDir + "/avatars");
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // 5. 保存文件
        File destFile = new File(uploadDirFile, filename);
        file.transferTo(destFile);

        // 6. 返回文件访问URL
        return "/api/file/avatars/" + filename;
    }
}
