package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.pojo.Teacher;

import java.util.List;

public interface AiService {

    /**
     * 获取AI的回答
     * @param question 问题
     * @return AI的回答
     */
    String chat(String question);

    /**
     * 异步获取AI数据并保存
     * @param userId 用户ID
     */
    void fetchAiData(Long userId);

    /**
     * 获取学生的向量表示（用于匹配）
     * @param student 学生对象
     * @return 向量列表 [0.1, 0.5, ...]
     */
    List<Double> getVectorFromAi(Student student);

    /**
     * 使用AI对学生进行排序
     * @param students 学生列表
     * @param dto 匹配参数
     * @return 排序后的学生ID列表
     */
    List<Long> rankStudentsByAi(List<Student> students, MatchStudentDTO dto);

    /**
     * 使用AI对老师进行排序
     * @param teachers 老师列表
     * @param dto 匹配参数
     * @return 排序后的老师ID列表
     */
    List<Long> rankTeachersByAi(List<Teacher> teachers, MatchTeacherDTO dto);
}
