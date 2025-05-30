package org.tutorial.tutorial_platform.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.TeachGrade;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;
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
@RequiredArgsConstructor
public class MatchServiceImp implements MatchService {

    /**
     * 教师数据访问接口，提供对 Teacher 实体的增删改查操作
     */
    private final TeacherRepository teacherRepository;

    /**
     * 学生数据访问接口，提供对 Student 实体的增删改查操作
     */
    private final StudentRepository studentRepository;

    /**
     * AI 服务接口，用于获取学生的 AI 向量表示
     */
    private final AiService aiService;

    /**
     * Jackson 提供的 JSON 解析器，用于将字符串转换为 List<Double>
     */
    private final ObjectMapper objectMapper;

    /**
     * 保存学生时自动生成向量并存入数据库
     *
     * @param student 待保存的学生对象
     * @return 已保存的学生对象
     */
    @Override
    public Student saveStudentWithVector(Student student) {
        // 调用 AI 接口获取该学生的向量表示
        List<Double> vector = aiService.getVectorFromAi(student);
        // 将向量转为字符串格式并设置到实体中
        student.setVector(vector.toString());
        // 保存学生及其向量信息
        return studentRepository.save(student);
    }

    /**
     * 保存老师时自动生成向量并存入数据库
     *
     * @param teacher 待保存的教师对象
     * @return 已保存的教师对象
     */
    @Override
    public Teacher saveTeacherWithVector(Teacher teacher) {
        // 获取老师的向量表示
        List<Double> vector = aiService.getVectorFromAi(teacher);
        // 设置向量字段
        teacher.setVector(vector.toString());
        // 保存老师及其向量信息
        return teacherRepository.save(teacher);
    }

    /**
     * 解析 JSON 字符串格式的向量为 List<Double>
     *
     * @param vectorStr 数据库中的向量字符串
     * @return 解析后的 Double 列表
     */
    private List<Double> parseVector(String vectorStr) {
        try {
            return Arrays.asList(objectMapper.readValue(vectorStr, Double[].class));
        } catch (Exception e) {
            throw new RuntimeException("解析向量失败", e);
        }
    }

    /**
     * 获取教师的向量 Map
     *
     * @param teachers 教师列表
     * @return 教师 -> 向量 映射表
     */
    private Map<Teacher, List<Double>> getTeacherVectors(List<Teacher> teachers) {
        Map<Teacher, List<Double>> result = new HashMap<>();
        for (Teacher t : teachers) {
            if (t.getVector() != null && !t.getVector().isEmpty()) {
                result.put(t, parseVector(t.getVector()));
            }
        }
        return result;
    }

    /**
     * 获取学生的向量 Map
     *
     * @param students 学生列表
     * @return 学生 -> 向量 映射表
     */
    private Map<Student, List<Double>> getStudentVectors(List<Student> students) {
        Map<Student, List<Double>> result = new HashMap<>();
        for (Student s : students) {
            if (s.getVector() != null && !s.getVector().isEmpty()) {
                result.put(s, parseVector(s.getVector()));
            }
        }
        return result;
    }

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
    private List<Teacher> rankTeachersByVector(List<Teacher> teachers, List<Double> targetVector) {
        Map<Teacher, List<Double>> teacherVectors = getTeacherVectors(teachers);
        Map<Teacher, Double> similarityMap = new HashMap<>();

        for (Map.Entry<Teacher, List<Double>> entry : teacherVectors.entrySet()) {
            similarityMap.put(entry.getKey(), cosineSimilarity(entry.getValue(), targetVector));
        }

        return similarityMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * 将学生按与目标向量的相似度排序
     *
     * @param students   候选学生列表
     * @param targetVector 目标向量
     * @return 按相似度降序排列的学生列表
     */
    private List<Student> rankStudentsByVector(List<Student> students, List<Double> targetVector) {
        Map<Student, List<Double>> studentVectors = getStudentVectors(students);
        Map<Student, Double> similarityMap = new HashMap<>();

        for (Map.Entry<Student, List<Double>> entry : studentVectors.entrySet()) {
            similarityMap.put(entry.getKey(), cosineSimilarity(entry.getValue(), targetVector));
        }

        return similarityMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * 基于已有向量匹配最适合的老师列表
     *
     * @param dto 前端传入的筛选条件及分页参数
     * @return 分页结果，包含按相似度排序的教师 VO 列表
     */
    @Override
    public Page<MatchTeacherVO> findTeachersWithAiRanking(MatchStudentDTO dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());

        // Step 1: 先根据科目、年级筛选出候选老师
        // 将字符串 grade 转换为 TeachGrade 枚举
        TeachGrade teachGrade;
        try {
            teachGrade = TeachGrade.valueOf(dto.getGrade().toUpperCase()); // 假设枚举命名规范为大写
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的年级类型：" + dto.getGrade());
        }

        Page<Teacher> pageFromDb = teacherRepository.findAllBySubjectAndTeachGrade(
                dto.getSubject(),
                teachGrade,
                pageRequest);

        List<Teacher> teachers = pageFromDb.getContent();

        if (teachers.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        List<Double> studentVector = parseVector(student.getVector());

        List<Teacher> sortedTeachers = rankTeachersByVector(teachers, studentVector);

        List<MatchTeacherVO> vos = sortedTeachers.stream()
                .map(this::toMatchTeacherVO)
                .collect(Collectors.toList());

        return new PageImpl<>(vos, pageRequest, pageFromDb.getTotalElements());
    }

    /**
     * 基于已有向量匹配最适合的学生列表
     *
     * @param dto 前端传入的筛选条件及分页参数
     * @return 分页结果，包含按相似度排序的学生 VO 列表
     */
    @Override
    public Page<MatchStudentVO> findStudentsWithAiRanking(MatchTeacherDTO dto) {
        // 构建分页请求
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());

        // Step 1: 先根据性别、科目、年级筛选出候选学生
        Page<Student> pageFromDb = studentRepository.findAllBySubjectAndGrade(
                dto.getSubject(),
                dto.getGrade(),
                pageRequest);

        List<Student> students = pageFromDb.getContent();

        // 如果没有候选人，直接返回空结果
        if (students.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        // Step 2: 获取当前老师的向量（来自数据库）
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("老师不存在"));

        List<Double> teacherVector = parseVector(teacher.getVector());

        // Step 3: 按已有向量计算相似度并排序
        List<Student> sortedStudents = rankStudentsByVector(students, teacherVector);

        // Step 4: 转换为 VO 并返回分页结果
        List<MatchStudentVO> vos = sortedStudents.stream()
                .map(this::toMatchStudentVO)
                .collect(Collectors.toList());

        return new PageImpl<>(vos, pageRequest, pageFromDb.getTotalElements());
    }

    /**
     * 将 Teacher 对象转换为 MatchTeacherVO
     *
     * @param t 教师对象
     * @return 匹配教师 VO
     */
    private MatchTeacherVO toMatchTeacherVO(Teacher t) {
        MatchTeacherVO vo = new MatchTeacherVO();
        vo.setUserId(t.getUser().getUserId());
        vo.setUsername(t.getUser().getUsername());
        vo.setGender(t.getGender());
        vo.setEducation(t.getEducation());
        vo.setTeachGrade(t.getTeachGrade());
        vo.setSubject(t.getSubject());
        vo.setScore(t.getScore());
        vo.setExperience(t.getExperience());
        vo.setHobby(t.getHobby());
        return vo;
    }

    /**
     * 将 Student 对象转换为 MatchStudentVO
     *
     * @param s 学生对象
     * @return 匹配学生 VO
     */
    private MatchStudentVO toMatchStudentVO(Student s) {
        MatchStudentVO vo = new MatchStudentVO();
        vo.setUserId(s.getUser().getUserId());
        vo.setUsername(s.getUser().getUsername());
        vo.setGender(s.getGender());
        vo.setGrade(s.getGrade());
        vo.setSubject(s.getSubject());
        vo.setScore(s.getScore());
        vo.setHobby(s.getHobby());
        vo.setGoal(s.getGoal());
        return vo;
    }
}
