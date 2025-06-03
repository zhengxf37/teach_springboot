package org.tutorial.tutorial_platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.dto.StudentInfoUpdateDTO;
import org.tutorial.tutorial_platform.dto.TeacherInfoUpdateDTO;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.service.UserInfoService;
import org.tutorial.tutorial_platform.vo.StudentInfoVO;
import org.tutorial.tutorial_platform.vo.TeacherInfoVO;
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
@Slf4j
public class UserInfoController {

    @Autowired
    private UserInfoService userService;

    /**
     * 查询用户个人信息接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @return 用户信息响应对象
     */
    @GetMapping("/info/user")
    public ResponseEntity<UserInfoVO> getUserInfo(HttpServletRequest request) {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询用户信息，用户ID：{}", userId);
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    /**
     * 获取学生信息
     * @param request
     * @return
     */
    @GetMapping("/info/student")
    public ResponseEntity<StudentInfoVO> getStudentInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询学生信息，用户ID：{}", userId);
        return ResponseEntity.ok(userService.getStudentInfo(userId));
    }

    /**
     * 获取教师信息
     * @param request
     * @return
     */
    @GetMapping("/info/teacher")
    public ResponseEntity<TeacherInfoVO> getTeacherInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询教师信息，用户ID：{}", userId);
        return ResponseEntity.ok(userService.getTeacherInfo(userId));
    }



    /**
     * 修改用户个人信息接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @param userInfoUpdateDTO 用户信息更新对象
     * @return 用户信息响应对象
     */
    @PostMapping("/update/user")
    public ResponseEntity<UserInfoVO> updateUserInfo(
            HttpServletRequest request,
            @RequestBody UserInfoUpdateDTO userInfoUpdateDTO) {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        userInfoUpdateDTO.setUserId(userId);
        log.info("修改用户信息，用户ID：{}", userId);
        return ResponseEntity.ok(userService.updateUserInfo(userInfoUpdateDTO));
    }
    /**
     * 修改教师个人信息接口
     * @param request HTTP请求对象，包含token中的用户信息
     * @param teacherInfoUpdateDTO 用户信息更新对象
     * @return 老师信息响应对象
     * 示范POST
     * {
     *   "gender": "女",
     *   "education": "硕士",
     *   "teachGrade": "初中",
     *   "subject": "物理"
     * }
     */
    @PostMapping("/update/teacher")
    public ResponseEntity<TeacherInfoVO> updateTeacherInfo(
            HttpServletRequest request,
            @RequestBody TeacherInfoUpdateDTO teacherInfoUpdateDTO) {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        teacherInfoUpdateDTO.setUserId(userId);
        log.info("修改教师信息，用户ID：{}", userId);
        return ResponseEntity.ok(userService.updateTeacherInfo(teacherInfoUpdateDTO));
    }

    /**
     * 修改学生个人信息接口
     * @param request
     * @param studentInfoUpdateDTO
     * @return
     */
    @PostMapping("/update/student")
    public ResponseEntity<StudentInfoVO> updateStudentInfo(
            HttpServletRequest request,
            @RequestBody StudentInfoUpdateDTO studentInfoUpdateDTO) {
        // 从token中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        studentInfoUpdateDTO.setUserId(userId);
        log.info("修改学生信息，用户ID：{}", userId);
        return ResponseEntity.ok(userService.updateStudentInfo(studentInfoUpdateDTO));
    }


}
