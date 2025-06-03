package org.tutorial.tutorial_platform.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.stream.Collectors;


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
    private final ObjectMapper objectMapper;
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
        List<Double> teacherVector = teacher.getVector();
        List<Student> students1 = studentRepository.findAll();
        // 筛选符合条件的学生
        List<Student> students = students1.stream()
                .filter(student -> {
                    boolean subjectMatch = matchTeacherDTO.getSubject().equals("-1") ||
                            student.getSubject().equals(matchTeacherDTO.getSubject());
                    boolean gradeMatch = matchTeacherDTO.getGrade().equals("-1") ||
                            student.getGrade().equals(matchTeacherDTO.getGrade());
                    return subjectMatch && gradeMatch;
                })
                .collect(Collectors.toList());
        students.sort((s1, s2) -> {
            return Double.compare(
                    calculateSimilarity(teacherVector, s1.getVector()),
                    calculateSimilarity(teacherVector, s2.getVector())
            );
        });
        int page = matchTeacherDTO.getPage();
        int size = matchTeacherDTO.getSize();
        int start = page * size;
        int end = Math.min(start + size, students.size());
        List<MatchStudentVO> voList = students.stream()
                .map(student -> new MatchStudentVO(student))
                .collect(Collectors.toList());
        // 构造分页对象
        List<MatchStudentVO> pagedContent;
        if (start > voList.size()) {
            // 如果起始位置超出范围，返回空分页
            pagedContent = Collections.emptyList();
        } else {
            int safeEnd = Math.min(end, voList.size());
            pagedContent = voList.subList(start, safeEnd);
        }
        return new PageImpl<>(pagedContent, PageRequest.of(page, size), voList.size());
    }

    /**
     * 根据学生匹配条件（如科目、年级）获取 AI 排序后的老师列表
     *
     * @param matchStudentDTO 前端传入的匹配参数，包含分页信息及筛选条件
     * @return 分页结果，包含按相似度排序的教师 VO 列表
     */
    public Page<MatchTeacherVO> findTeachersWithAi(MatchStudentDTO matchStudentDTO) throws JsonProcessingException {

        // 1获取目标学生的向量
        Long studentId = matchStudentDTO.getStudentId();
        //存储的是用户id
        Student student = studentRepository.findByUserUserId(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        List<Double> studentVector = student.getVector();
        List<Teacher> teachersa = teacherRepository.findAll();
        // 筛选符合条件的教师
        List<Teacher> teachers = teachersa.stream()
                .filter(teacher -> {
                    // 匹配科目
                    boolean subjectMatch = matchStudentDTO.getSubject().equals("-1") ||
                            teacher.getSubject().equals(matchStudentDTO.getSubject());
                    // 匹配年级
                    boolean gradeMatch = matchStudentDTO.getGrade().equals("-1") ||
                            teacher.getTeachGrade().equals(matchStudentDTO.getGrade());
                    log.info("teacher.getSubject():"+teacher.getSubject());
                    log.info("teacher.getTeachGrade():"+teacher.getTeachGrade());
                    log.info("match:"+(subjectMatch)+":"+gradeMatch);
                    return subjectMatch && gradeMatch;
                })
                .collect(Collectors.toList());

        // 3. 计算相似度并排序
        teachers.sort((t1, t2) -> {
            double sim1 = calculateSimilarity(studentVector, t1.getVector());
            double sim2 = calculateSimilarity(studentVector, t2.getVector());
            return Double.compare(sim2, sim1); // 降序排列
        });
        int page = matchStudentDTO.getPage();
        int size = matchStudentDTO.getSize();
        int start = page * size;
        int end = Math.min(start + size, teachers.size());
        List<MatchTeacherVO> voList = teachers.stream()
                .map(teacher -> new MatchTeacherVO(teacher))
                .collect(Collectors.toList());
        // 构造分页对象
        List<MatchTeacherVO> pagedContent;
        if (start > voList.size()) {
            // 如果起始位置超出范围，返回空分页
            pagedContent = Collections.emptyList();
        } else {
            int safeEnd = Math.min(end, voList.size());
            pagedContent = voList.subList(start, safeEnd);
        }
        return new PageImpl<>(pagedContent, PageRequest.of(page, size), voList.size());
    }
//        String studentVectorJson = objectMapper.writeValueAsString(student.getVector());
//        log.info("开始查询老师");
//        log.info("传入科目{}，年级{}向量{}",  matchStudentDTO.getSubject(), matchStudentDTO.getGrade(), studentVectorJson);
        // 调用新方法（替换原有分页查询）
//        Page<Teacher> teacherPage = teacherRepository.findAndSortByVector(
//                studentVectorJson,
//                matchStudentDTO.getSubject(),
//                matchStudentDTO.getGrade(),
//                PageRequest.of(matchStudentDTO.getPage(), matchStudentDTO.getSize())
//        );


//        log.info(String.valueOf(teacherPage.getTotalElements()));
//        for (Teacher teacher : teacherPage) {
//            log.info(teacher.getSubject());
//        }
//        // 转换 VO（保持原有逻辑）
//        return teacherPage.map(teacher -> {
//            MatchTeacherVO vo = new MatchTeacherVO(teacher);
//
//
//            return vo;
//        });
//    }
    //工具
    private double calculateSimilarity(List<Double> vecA, List<Double> vecB) {
        if (vecA == null || vecB == null || vecA.size() != vecB.size()) {
            return 0.0;
        }

        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < vecA.size(); i++) {
            double a = vecA.get(i);
            double b = vecB.get(i);
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }



}
