package org.tutorial.tutorial_platform.service;

import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.dto.StudentInfoUpdateDTO;
import org.tutorial.tutorial_platform.dto.TeacherInfoUpdateDTO;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.vo.StudentInfoVO;
import org.tutorial.tutorial_platform.vo.TeacherInfoVO;
import org.tutorial.tutorial_platform.vo.UserInfoVO;

import java.io.IOException;

/**
 * UserInfoService - 用户信息服务接口
 * 
 * 提供用户信息相关的核心业务逻辑，包括：
 * - 查询用户信息：根据用户ID获取用户详细信息
 * - 更新用户信息：修改用户的基本信息
 * - 头像管理：上传和更新用户头像
 * 
 * 安全说明：
 * - 所有方法都需要验证用户身份
 * - 用户只能操作自己的信息
 * 
 * 元信息：
 * @author 周宏杰
 * @version 1.0
 * @since 2025-05-13
 */
public interface UserInfoService {
    /**
     * 获取用户详细信息
     * @param userId 用户ID
     * @return 用户信息视图对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 获取学生信息
     * @param userId 用户ID
     * @return 学生信息视图对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    StudentInfoVO getStudentInfo(Long userId);
    /**
     * 获取教师信息
     * @param userId 用户ID
     * @return 教师信息视图对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    TeacherInfoVO getTeacherInfo(Long userId);
    /**
     * 更新用户信息
     * @param userInfoUpdateDTO 用户信息更新对象，包含：
     *                         - 用户名
     *                         - 密码（用于验证）
     *                         - 邮箱
     *                         - 用户类型
     * @return 更新后的用户信息视图对象
     * @throws RuntimeException 当以下情况发生时抛出：
     *                         - 用户不存在
     *                         - 密码验证失败
     *                         - 用户名/邮箱已被使用
     */
    UserInfoVO updateUserInfo(UserInfoUpdateDTO userInfoUpdateDTO);

    /**
     * 更新教师信息
     * @param teacherInfoUpdateDTO 教师信息更新对象，包含：
     *                            - 教师ID
     *                            - 性别
     *                            - 学历
     *                            - 教学年级
     *                            - 科目
     * @return 更新后的教师信息视图对象
     * @throws RuntimeException 当以下情况发生时抛出：
     *                         - 教师不存在
     *                         - 教师信息更新失败
     */
    TeacherInfoVO updateTeacherInfo(TeacherInfoUpdateDTO teacherInfoUpdateDTO) throws RuntimeException;

    /**
     * 更新学生信息
     *
     * @param studentInfoUpdateDTO 包含学生信息更新的数据传输对象
     *                            - 性别
     *                            - 年级
     *                            - 科目
     * @return 更新后的学生信息视图对象
     * @throws RuntimeException 当以下情况发生时抛出：
     *                         - 学生不存在
     *                         - 学生信息更新失败
     */
    StudentInfoVO updateStudentInfo(StudentInfoUpdateDTO studentInfoUpdateDTO);
}
