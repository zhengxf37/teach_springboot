package org.tutorial.tutorial_platform.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    private final AiService aiService;
    private final ObjectMapper objectMapper;

    /**
     * 保存用户时自动生成向量并存入数据库
     *
     * @param userId
     * @return 已保存的学生对象
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
            List<Double> vector = Arrays.asList(1.0,1.8,5.6);
            // 将向量转为字符串格式并设置到实体中
//            String vectorStr = objectMapper.writeValueAsString(vector); // 转为字符串

            student.setVector(vector); // 存入数据库字段（varchar/text 类型）
            // 保存学生及其向量信息
            studentRepository.save(student);
        }else if (user.getUserType() == UserType.TEACHER) {

            Teacher teacher = teacherRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("教师信息不存在"));
            // 调用 AI 接口获取表示
            Double[] vector = new Double[]{1.0,1.8,5.6};
//            String vectorStr = objectMapper.writeValueAsString(vector);
            teacher.setVector(vector);
            teacherRepository.save(teacher);


        }else{
            throw new RuntimeException("用户类型不存在");
        }

        log.info("ai保存向量成功");
    }



/*
    /**
     * 计算两个向量之间的余弦相似度
     *
     * @param vec1 第一个向量
     * @param vec2 第二个向量
     * @return 相似度值（0~1）
     */
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("向量维度不一致");
        }

        double dotProduct = 0.0, magnitude1 = 0.0, magnitude2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            magnitude1 += Math.pow(vec1.get(i), 2);
            magnitude2 += Math.pow(vec2.get(i), 2);
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    /**
     * 将教师按与目标向量的相似度排序
     *
     * @param teachers   候选教师列表
     * @param targetVector 目标向量
     * @return 按相似度降序排列的教师列表
     */
    private List<Teacher> rankTeachersByVector(List<Teacher> teachers, List<Double> targetVector) throws JsonProcessingException {
//        1读取老师向量
        Map<Long, List<Double> > teacherVectors = new HashMap<>();
        for (Teacher t : teachers) {
            teacherVectors.put(t.getTeacherId(), objectMapper.readValue(t.getVector(), new TypeReference<List<Double>>() {}));
        }
        //2排序
//        teachers.sort((t1, t2) -> {
//            return Double.compare(), targetVector));
//        });
        return teachers.stream()
                .filter(t -> teacherVectors.containsKey(t.getTeacherId())) // 排除没有向量的教师
                .sorted((t1, t2) -> {
                    List<Double> v1 = teacherVectors.get(t1.getTeacherId());
                    List<Double> v2 = teacherVectors.get(t2.getTeacherId());

                    double similarity1 = cosineSimilarity(v1, targetVector);
                    double similarity2 = cosineSimilarity(v2, targetVector);

                    return Double.compare(similarity2, similarity1); // 降序排列
                })
                .toList();
    }

    /**
     * 将学生按与目标向量的相似度排序
     *
     * @param students   候选学生列表
     * @param targetVector 目标向量
     * @return 按相似度降序排列的学生列表
     */
    private List<Student> rankStudentsByVector(List<Student> students, List<Double> targetVector) throws JsonProcessingException {
        Map<Long, List<Double> > studentVectors = new HashMap<>();

        for (Student s : students) {
            studentVectors.put(s.getStudentId(), objectMapper.readValue(s.getVector(), new TypeReference<List<Double>>() {}));
        }

        return students.stream()
                .filter(s -> studentVectors.containsKey(s.getStudentId())) // 排除没有向量的学生
                .sorted((s1, s2) -> {
                    List<Double> v1 = studentVectors.get(s1.getStudentId());
                    List<Double> v2 = studentVectors.get(s2.getStudentId());

                    double similarity1 = cosineSimilarity(v1, targetVector);
                    double similarity2 = cosineSimilarity(v2, targetVector);

                    return Double.compare(similarity2, similarity1); // 降序排列
                })
                .toList();
    }
*/
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
        // 调用新方法（替换原有分页查询）
        Page<Teacher> teacherPage = teacherRepository.findAndSortByVector(
                student.getVector(),
                matchStudentDTO.getSubject(),
                matchStudentDTO.getGrade(),
                PageRequest.of(matchStudentDTO.getPage(), matchStudentDTO.getSize())
        );
        // 转换 VO（保持原有逻辑）
        return teacherPage.map(teacher -> {
            MatchTeacherVO vo = new MatchTeacherVO();
            BeanUtils.copyProperties(teacher, vo);

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
    public Page<MatchStudentVO> findStudentsWithAi(MatchTeacherDTO matchTeacherDTO) throws JsonProcessingException {
        // 构建分页请求
        PageRequest pageRequest = PageRequest.of(matchTeacherDTO.getPage(), matchTeacherDTO.getSize());
        // Step 1: 先根据科目、年级筛选出候选学生
        Page<Student> pageFromDb = studentRepository.findAllBySubjectAndGrade(
                matchTeacherDTO.getSubject(),
                matchTeacherDTO.getGrade(),
                pageRequest);
        List<Student> students = pageFromDb.getContent();
        // 如果没有候选人，直接返回空结果
        if (students.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }
        // Step 2: 获取当前老师的向量（来自数据库）
        //  存储的是用户id
        Teacher teacher = teacherRepository.findByUserUserId(matchTeacherDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("老师不存在"));

        //反序列化
        List<Double> teacherVector = objectMapper.readValue(teacher.getVector(), new TypeReference<List<Double>>() {});
        List<Student> sortedStudents = rankStudentsByVector(students, teacherVector);
        List<MatchStudentVO> vos = sortedStudents.stream()
                .map(student -> {
                    MatchStudentVO vo = new MatchStudentVO();
                    BeanUtils.copyProperties(vo, student);
                    return vo;
                })
                .toList();
        return new PageImpl<>(vos, pageRequest, pageFromDb.getTotalElements());
    }
}
