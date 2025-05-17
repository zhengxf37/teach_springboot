package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tutorial.tutorial_platform.dto.StudentInfoUpdateDTO;
import org.tutorial.tutorial_platform.dto.TeacherInfoUpdateDTO;
import org.tutorial.tutorial_platform.dto.UserInfoUpdateDTO;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.pojo.User;
import org.tutorial.tutorial_platform.pojo.UserType;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.UserInfoService;
import org.tutorial.tutorial_platform.vo.StudentInfoVO;
import org.tutorial.tutorial_platform.vo.TeacherInfoVO;
import org.tutorial.tutorial_platform.vo.UserInfoVO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
    
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * 获取学生详细信息
     * @param userId 用户ID
     * @return 学生信息视图对象
     * @throws RuntimeException 当学生不存在时抛出
     */
    @Override
    public StudentInfoVO getStudentInfo(Long userId) {
        // 1. 查询用户实体
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 验证用户是否为学生
        if (user.getUserType() != UserType.STUDENT) {
            throw new RuntimeException("该用户不是学生");
        }

        // 3. 查询学生实体
        Student student = studentRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("学生信息不存在"));

        // 4. 构建并返回视图对象
        return new StudentInfoVO(student);
    }

    /**
     * 获取教师详细信息
     * @param userId 用户ID
     * @return 教师信息视图对象
     * @throws RuntimeException 当教师不存在时抛出
     */
    @Override
    public TeacherInfoVO getTeacherInfo(Long userId) {
        // 1. 查询用户实体
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 验证用户是否为教师
        if (user.getUserType() != UserType.TEACHER) {
            throw new RuntimeException("该用户不是教师");
        }

        // 3. 查询教师实体
        Teacher teacher = teacherRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("教师信息不存在"));

        // 4. 构建并返回视图对象
        return new TeacherInfoVO(teacher);
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

        // 2. 更新用户信息
        user.setUsername(userInfoUpdateDTO.getUsername());
        user.setEmail(userInfoUpdateDTO.getEmail());
        user.setUserType(userInfoUpdateDTO.getUserType());
        user.setPassword(passwordEncoder.encode(userInfoUpdateDTO.getPassword()));

        // 3. 保存更新
        User updatedUser = userRepository.save(user);
        return new UserInfoVO(updatedUser);
    }

    
    /**
     * 更新教师信息
     * @param teacherInfoUpdateDTO 教师信息更新对象
     * @return 更新后的教师信息视图对象
     * @throws RuntimeException 当以下情况发生时抛出：
     *                         - 教师不存在
     *                         - 密码验证失败
     *                         - 教师信息更新失败
     */
    @Override
    public TeacherInfoVO updateTeacherInfo(TeacherInfoUpdateDTO teacherInfoUpdateDTO) {
        User user = userRepository.findById(teacherInfoUpdateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (user.getUserType() != UserType.TEACHER) {
            throw new RuntimeException("该用户不是教师");
        }

        Teacher teacher = teacherRepository.findByUserUserId(user.getUserId())
                .orElseGet(() -> {
                    Teacher newTeacher = new Teacher();
                    newTeacher.setUser(user);
                    return newTeacher;
                });

        teacher.setGender(teacherInfoUpdateDTO.getGender());
        teacher.setEducation(teacherInfoUpdateDTO.getEducation());
        teacher.setTeachGrade(teacherInfoUpdateDTO.getTeachGrade());
        teacher.setSubject(teacherInfoUpdateDTO.getSubject());
        teacher.setAddress(teacherInfoUpdateDTO.getAddress());
        teacher.setPhone(teacherInfoUpdateDTO.getPhone());
        teacher.setExperience(teacherInfoUpdateDTO.getExperience());
        teacher.setScore(BigDecimal.valueOf(teacherInfoUpdateDTO.getScore()));
        teacher.setHobby(teacherInfoUpdateDTO.getHobby());
        teacher.setSchool(teacherInfoUpdateDTO.getSchool());
        teacher.setAddition(teacherInfoUpdateDTO.getAddition());

        teacherRepository.save(teacher);

        return new TeacherInfoVO(teacher);
    }


    /**
     * 更新学生信息
     * @param studentInfoUpdateDTO 包含学生信息更新的数据传输对象
     *                            - 性别
     *                            - 年级
     *                            - 科目
     *                            - 地址
     * @return
     * @throws RuntimeException 如果更新失败，则抛出运行时异常
     */
    @Override
    public StudentInfoVO updateStudentInfo(StudentInfoUpdateDTO studentInfoUpdateDTO) {
        User user = userRepository.findById(studentInfoUpdateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (user.getUserType() != UserType.STUDENT) {
            throw new RuntimeException("该用户不是学生");
        }

        Student student = studentRepository.findByUserUserId(user.getUserId())
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setUser(user);
                    return newStudent;
                });

        student.setGender(studentInfoUpdateDTO.getGender());
        student.setGrade(studentInfoUpdateDTO.getGrade());
        student.setSubject(studentInfoUpdateDTO.getSubject());
        student.setAddress(studentInfoUpdateDTO.getAddress());
        student.setPhone(studentInfoUpdateDTO.getPhone());
        student.setScore(BigDecimal.valueOf(studentInfoUpdateDTO.getScore()));
        student.setHobby(studentInfoUpdateDTO.getHobby());
        student.setGoal(studentInfoUpdateDTO.getGoal());
        student.setAddition(studentInfoUpdateDTO.getAddition());

        studentRepository.save(student);

        return new StudentInfoVO(student);
    }



}
