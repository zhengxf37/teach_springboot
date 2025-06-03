package org.tutorial.tutorial_platform.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.pojo.*;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.MatchService;
import org.tutorial.tutorial_platform.service.AiService;
import org.tutorial.tutorial_platform.vo.MatchStudentVO;
import org.tutorial.tutorial_platform.vo.MatchTeacherVO;

import java.util.*;

/**
 * 匹配服务实现类：基于 AI 向量进行学生与教师的智能匹配。
 * 功能说明：
 * - 学生/老师注册时自动生成并保存 AI 向量；
 * - 支持根据已有向量进行余弦相似度排序；
 * - 支持分页查询；
 * - VO 转换用于返回前端数据；
 * - 向量解析、相似度计算等功能封装在内部。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MatchServiceImp implements MatchService {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    private final AiService aiService;

    /**
     * 保存用户时自动生成向量并存入数据库
     *
     * @param userId 用户 ID
     */
    @Async
    @Override
    public void saveWithVector(Long userId) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getUserType() == UserType.STUDENT) {
            Student student = studentRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("学生信息不存在"));
            // 调用 AI 接口获取该学生的向量表示
            //TODO 获取向量
            List<Double> vector = aiService.getVectorFromAi(student);
//            List<Double> vector = Arrays.asList(1.0,1.8,5.6);
            student.setVector(vector); // 存入数据库字段（varchar/text 类型）
            // 保存学生及其向量信息
            studentRepository.save(student);
        }else if (user.getUserType() == UserType.TEACHER) {

            Teacher teacher = teacherRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("教师信息不存在"));
            List<Double> vector = aiService.getVectorFromAi(teacher);
            // 调用 AI 接口获取表示
//            List<Double> vector = Arrays.asList(1.0,1.8,5.6);

            teacher.setVector(vector);
            teacherRepository.save(teacher);


        }else{
            throw new RuntimeException("用户类型不存在");
        }

        log.info("ai保存向量成功");
    }




    /**
     * 根据学生匹配条件（如科目、年级）获取 AI 排序后的老师列表
     *
     * @param matchStudentDTO 前端传入的匹配参数，包含分页信息及筛选条件
     * @return 分页结果，包含按相似度排序的教师 VO 列表
     */
    public Page<MatchTeacherVO> findTeachersWithAi(MatchStudentDTO matchStudentDTO) {
        // 1获取目标学生的向量
        Long studentId = matchStudentDTO.getStudentId();
        //存储的是用户id
        Student student = studentRepository.findByUserUserId(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        // 调用新方法（替换原有分页查询）
        Page<Teacher> teacherPage = teacherRepository.findAndSortByVector(
                student.getVector(),
                matchStudentDTO.getSubject(),
                matchStudentDTO.getGrade(),
                PageRequest.of(matchStudentDTO.getPage(), matchStudentDTO.getSize())
        );
        // 转换 VO（保持原有逻辑）
        return teacherPage.map(teacher -> {
            MatchTeacherVO vo = new MatchTeacherVO(teacher);


            return vo;
        });
    }
    /**
     * 根据老师匹配条件（如科目、年级）获取 AI 排序后的学生列表
     *
     * @param matchTeacherDTO 前端传入的筛选条件及分页参数
     * @return 分页结果，包含按相似度排序的学生 VO 列表
     */
    @Override
    public Page<MatchStudentVO> findStudentsWithAi(MatchTeacherDTO matchTeacherDTO){
        Long teacherId = matchTeacherDTO.getTeacherId();
        Teacher teacher = teacherRepository.findByUserUserId(teacherId)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        Page<Student> studentPage = studentRepository.findAndSortByVector(
                teacher.getVector(),
                matchTeacherDTO.getSubject(),
                matchTeacherDTO.getGrade(),
                PageRequest.of(matchTeacherDTO.getPage(), matchTeacherDTO.getSize())
        );
        return studentPage.map(student -> {
            MatchStudentVO vo = new MatchStudentVO(student);
            return vo;
        });
    }
}
