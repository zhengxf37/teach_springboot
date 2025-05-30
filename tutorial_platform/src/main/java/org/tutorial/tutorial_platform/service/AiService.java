package org.tutorial.tutorial_platform.service;

import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.Teacher;
import java.util.List;

/**
 * AI 服务接口，定义与第三方 AI 接口交互的基本功能。
 * 功能包括：
 * - 向量生成（用于相似度匹配）
 * - 学生/教师列表智能排序
 * - 基础聊天能力
 */
public interface AiService {

    /**
     * 获取学生的 AI 向量表示（用于相似度匹配）
     *
     * @param student 学生对象
     * @return 数值向量列表，如 [0.1, 0.5, 0.3]
     */
    List<Double> getVectorFromAi(Student student);

    /**
     * 获取教师的 AI 向量表示（用于相似度匹配）
     *
     * @param teacher 教师对象
     * @return 数值向量列表，如 [0.1, 0.5, 0.3]
     */
    List<Double> getVectorFromAi(Teacher teacher);



    /**
     * 向 DeepSeek 发送请求并获取 AI 的回答
     *
     * @param question 用户输入的问题
     * @return AI 返回的回答内容
     */
    String chat(String question);

    /**
     * 异步调用 AI 获取用户数据并保存
     *
     * @param userId 用户 ID
     */
    void fetchAiData(Long userId);
}
