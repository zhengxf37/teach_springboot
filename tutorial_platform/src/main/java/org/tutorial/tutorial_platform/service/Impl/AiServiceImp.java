package org.tutorial.tutorial_platform.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.service.AiService;

/**
 * AI 服务实现类，负责与第三方 AI 接口交互，实现向量生成功能。
 * 功能说明：
 * - 使用 DeepSeek 的 API 为学生和老师生成匹配向量；
 * - 向量基于：性别、年级、科目、地址、手机号、评分、爱好、目标/补充信息；
 * - 向量用于本地余弦相似度排序；
 * - 所有 AI 调用都走异步，避免阻塞主线程；
 * - 提供统一接口供 MatchService 调用。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiServiceImp implements AiService{

    /**
     * Spring 提供的标准 HTTP 请求客户端
     * 用于向 DeepSeek 发送请求并获取响应
     */
    private final RestTemplate restTemplate;

    /**
     * Jackson 提供的 JSON 解析器
     * 用于将 AI 返回的字符串向量解析为 List<Double>
     */
    private final ObjectMapper objectMapper;




    // DeepSeek API 配置
    private static final String API_KEY = "sk-2cdaa628f72e496e9bd19ab75f9afb6a";
    private static final String BASE_URL = "https://api.deepseek.com";

    /**
     * 获取学生的 AI 向量表示（用于匹配）
     *
     * @param student 学生对象
     * @return 数值向量列表，如 [0.1, 0.5, 0.3]
     */
    @Override
    public List<Double> getVectorFromAi(Student student) {
        String prompt = buildStudentVectorPrompt(student);
        return callAiAndGetVector(prompt);
    }

    /**
     * 获取教师的 AI 向量表示（用于匹配）
     *
     * @param teacher 教师对象
     * @return 数值向量列表，如 [0.1, 0.5, 0.3]
     */
    @Override
    public List<Double> getVectorFromAi(Teacher teacher) {
        String prompt = buildTeacherVectorPrompt(teacher);
        return callAiAndGetVector(prompt);
    }

    /**
     * 构建学生的 AI 向量提示词（只保留指定字段）
     * 字段包括：
     * - 性别 (gender)
     * - 年级 (grade)
     * - 科目 (subject)
     * - 地址 (address)
     * - 手机号 (phone)
     * - 评分 (score)
     * - 爱好 (hobby)
     * - 目标 (goal)
     * - 补充信息 (addition)
     */
    private String buildStudentVectorPrompt(Student student) {
        return "请根据以下学生信息生成一个用于匹配的数值向量（如[0.1, 0.5, 0.3]），" +
                "向量空间中越接近的学生和老师越匹配。\n" +
                "性别：" + student.getGender() +
                ", 年级：" + student.getGrade() +
                ", 科目：" + student.getSubject() +
                ", 地址：" + student.getAddress() +
                ", 手机号：" + student.getPhone() +
                ", 评分：" + student.getScore() +
                ", 爱好：" + student.getHobby() +
                ", 目标：" + student.getGoal() +
                ", 补充信息：" + student.getAddition() +
                "\n请只返回向量，不要解释。";
    }

    /**
     * 构建教师的 AI 向量提示词（只保留指定字段）
     * 字段包括：
     * - 性别 (gender)
     * - 教授年级 (teachGrade)
     * - 科目 (subject)
     * - 地址 (address)
     * - 手机号 (phone)
     * - 评分 (score)
     * - 爱好 (hobby)
     * - 补充信息 (addition)
     */
    private String buildTeacherVectorPrompt(Teacher teacher) {
        return "请根据以下老师信息生成一个用于匹配的数值向量（如[0.1, 0.5, 0.3]），" +
                "向量空间中越接近的学生和老师越匹配。\n" +
                "性别：" + teacher.getGender() +
                ", 教授年级：" + teacher.getTeachGrade() +
                ", 科目：" + teacher.getSubject() +
                ", 地址：" + teacher.getAddress() +
                ", 手机号：" + teacher.getPhone() +
                ", 评分：" + teacher.getScore() +
                ", 爱好：" + teacher.getHobby() +
                ", 补充信息：" + teacher.getAddition() +
                "\n请只返回向量，不要解释。";
    }

    /**
     * 调用 AI 接口并获取向量结果（同步方法）
     *
     * @param prompt 包含实体信息的提示词
     * @return 解析后的 Double 向量列表
     */
    private List<Double> callAiAndGetVector(String prompt) {
        try {
            // 调用 AI 接口，获取原始字符串格式向量
            String aiResponse = chat(prompt).trim();

            // 检查格式是否合法
            if (!aiResponse.startsWith("[") || !aiResponse.endsWith("]")) {
                throw new RuntimeException("AI 返回的向量格式错误：" + aiResponse);
            }

            // 将字符串向量解析为 List<Double>
            return parseVector(aiResponse);

        } catch (Exception e) {
            log.error("调用 AI 生成向量失败", e);
            throw new RuntimeException("调用 AI 生成向量失败", e);
        }
    }

    /**
     * 向 DeepSeek 发送请求并获取 AI 的回答
     *
     * @param question 用户输入的问题
     * @return AI 返回的回答内容
     */
    @Override
    public String chat(String question) {
        try {


            // 构建请求体
            HttpEntity<String> requestEntity = buildRequestBody(question);

            // 发起 POST 请求
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    BASE_URL + "/chat/completions",
                    requestEntity,
                    String.class
            );

            // 判断响应状态码是否为 OK
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                log.warn("AI 接口返回空结果");
                return "[]"; // 默认空向量
            }
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用 DeepSeek API 失败", e);
        }
    }

    /**
     * 构建 HTTP 请求头
     * 包括认证 Token 和 Content-Type
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        return headers;
    }

    /**
     * 构建请求体
     *
     * @param question 提示词内容
     * @return 包含模型参数和消息的 JSON 请求体
     */
    private HttpEntity<String> buildRequestBody(String question) {
        String requestBody = String.format(
                "{\"model\": \"deepseek-chat\", \"messages\": [{\"role\": \"system\",\"content\": \"You are a helpful assistant.\"},{\"role\": \"user\",\"content\": \"%s\"}], \"stream\": false}",
                question.replace("\"", "\\\"")
        );
        return new HttpEntity<>(requestBody, buildHeaders());
    }

    /**
     * 将 AI 返回的字符串向量解析为 List<Double>
     * 示例输入："[0.1, 0.5]"
     * 输出：List<Double> [0.1, 0.5]
     */
    private List<Double> parseVector(String response) {
        try {
            return Arrays.asList(objectMapper.readValue(response, Double[].class));
        } catch (Exception e) {
            log.error("解析AI向量失败: {}", e.getMessage(), e);
            throw new RuntimeException("解析AI向量失败", e);
        }
    }

    /**
     * 异步调用 AI 获取用户数据并保存
     *
     * @param userId 用户 ID
     */
    @Override
    @Async
    public void fetchAiData(Long userId) {
        String requestBody =
                "{\"model\": \"deepseek-chat\", \"messages\": [{\"role\": \"system\",\"content\": \"You are a helpful assistant.\"},{\"role\": \"user\",\"content\": \"我如何选择一个合适的老师\"}], \"stream\": false}";


        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, buildHeaders());
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(BASE_URL + "/chat/completions", requestEntity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info("用户 {} 的 AI 数据已获取: {}", userId, responseEntity.getBody());
            } else {
                log.warn("用户 {} 的 AI 数据请求失败", userId);
            }
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败", e);
        }
    }
}
