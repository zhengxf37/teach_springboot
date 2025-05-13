package org.tutorial.tutorial_platform.service;


import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.vo.UserInfoVO;
/**
 * @Description:
 * @Author: 周宏杰
 */
public interface UserInfoService {
    UserInfoVO getUserInfo(Long userId);

    UserInfoVO updateUserInfo(UserInfoUpdateDTO userInfoUpdateDTO);
}
