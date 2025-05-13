package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.UserInfoService;
import org.tutorial.tutorial_platform.vo.UserInfoVO;

import java.util.Optional;

/**
 * 用户信息服务实现类
 * @author: 周宏杰
 */
@Service
public class UserInfoServiceImp implements UserInfoService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户ID查询用户信息并返回UserInfoVO对象
     * @param userId 用户唯一标识
     * @return UserInfoVO 包含用户详细信息的视图对象
     * @throws RuntimeException 如果用户不存在
     */
    public UserInfoVO getUserInfo(Long userId) {
        // 1. 查询用户实体
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();

        // 2. 构建 UserInfoVO 对象
        UserInfoVO userInfoVO = new UserInfoVO(user);

        return userInfoVO;
    }

    /**
     * 更新用户信息
     * @param userInfoUpdateDTO
     * @return
     */
    public UserInfoVO updateUserInfo(UserInfoUpdateDTO userInfoUpdateDTO) {
        // 1. 查询用户实体
        Optional<User> userOptional = userRepository.findById(userInfoUpdateDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();

        // 2. 更新用户信息
        user.setUsername(userInfoUpdateDTO.getUsername());
        user.setEmail(userInfoUpdateDTO.getEmail());
        user.setUserType(userInfoUpdateDTO.getUserType());
        // 3. 保存更新后的用户信息
        userRepository.save(user);
        return new UserInfoVO(user);
    }
}
