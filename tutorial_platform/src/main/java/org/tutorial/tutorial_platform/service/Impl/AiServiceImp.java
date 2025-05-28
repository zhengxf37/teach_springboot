package org.tutorial.tutorial_platform.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

import org.tutorial.tutorial_platform.dto.MatchStudentDTO;
import org.tutorial.tutorial_platform.dto.MatchTeacherDTO;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.service.AiService;

/**
 * AI 服务实现类，负责与第三方 AI 接口（如 DeepSeek）进行交互，
 * 实现向量生成、智能排序等功能。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiServiceImp implements AiService {

    /**
     * 用于发起 HTTP 请求的客户端，Spring 提供的标准 REST 客户端
     */
    private final RestTemplate restTemplate;

    /**
     * 用于解析 JSON 格式的 AI 响应结果
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 向 DeepSeek 发送请求并获取 AI 的回答
     *
     * @param question 用户输入的问题
     * @return AI 返回的回答内容
     */
    @Override
    public String chat(String question) {
        try {
            // 设置 API 密钥和基础 URL
            String apiKey = "sk-2cdaa628f72e496e9bd19ab75f9afb6a";
            String baseUrl = "https://api.deepseek.com";

            // 构建请求体 JSON 数据
            String requestBody = "{"
                    + "\"model\": \"deepseek-chat\","
                    + " \"messages\": [{\"role\": \"system\",\"content\": \"You are a helpful assistant.\"},{\"role\": \"user\",\"content\": \"" + question + "\"}],"
                    + "\"stream\": false}";

            // 设置请求头信息
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 封装请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发起 POST 请求
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/chat/completions", requestEntity, String.class);

            // 判断响应状态码是否为 200 OK
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                throw new RuntimeException("AI 接口返回空结果");
            }
        } catch (Exception e) {
            throw new RuntimeException("调用 DeepSeek API 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取学生的 AI 向量表示（用于相似度匹配）
     *
     * @param student 学生对象
     * @return 数值向量列表，如 [0.1, 0.5, 0.3]
     */
    @Override
    public List<Double> getVectorFromAi(Student student) {
        // 构建提示词，描述需要生成的向量格式和学生信息
        String prompt = buildVectorPrompt(student);

        // 调用 AI 获取响应结果
        String aiResponse = chat(prompt).trim();

        // 检查返回结果是否是合法的向量格式
        if (!aiResponse.startsWith("[") || !aiResponse.endsWith("]")) {
            throw new RuntimeException("AI 返回的向量格式错误：" + aiResponse);
        }

        // 解析字符串为 Double 列表
        return parseVector(aiResponse);
    }

    /**
     * 构建生成向量的提示词（prompt），描述学生特征
     *
     * @param student 学生对象
     * @return 构建好的提示词文本
     */
    private String buildVectorPrompt(Student student) {
        return "请根据以下学生信息生成一个用于匹配的数值向量（如[0.1, 0.5, 0.3]），" +
                "向量空间中越接近的学生和老师越匹配。\n" +
                "科目：" + student.getSubject() + ", 年级：" + student.getGrade() +
                ", 爱好：" + student.getHobby() + ", 目标：" + student.getGoal() +
                "\n请只返回向量，不要解释。";
    }

    /**
     * 将 AI 返回的字符串向量解析为 List<Double>
     *
     * @param response AI 返回的向量字符串，如 "[0.1, 0.5]"
     * @return 解析后的 Double 列表
     */
    private List<Double> parseVector(String response) {
        try {
            return Arrays.asList(objectMapper.readValue(response, Double[].class));
        } catch (Exception e) {
            throw new RuntimeException("解析AI向量失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 AI 对老师进行排序，返回排序后的 ID 列表
     *
     * @param teachers 老师列表
     * @param dto      匹配参数
     * @return 排序后的老师 ID 列表
     */
    @Override
    public List<Long> rankTeachersByAi(List<Teacher> teachers, MatchTeacherDTO dto) {
        String prompt = buildRankingPrompt(dto, teachers);
        String aiResponse = chat(prompt).trim();
        return parseAiRanking(aiResponse);
    }

    /**
     * 构建对老师进行排序的提示词（prompt）
     *
     * @param dto      匹配参数
     * @param teachers 老师列表
     * @return 构建好的提示词文本
     */
    private String buildRankingPrompt(MatchTeacherDTO dto, List<Teacher> teachers) {
        StringBuilder sb = new StringBuilder();
        sb.append("请根据以下学生需求和老师信息，按照匹配程度从高到低排序老师，并返回老师ID列表，逗号分隔。\n");
        sb.append("学生需求：科目=").append(dto.getSubject())
                .append(", 年级=").append(dto.getGrade())
                .append(", 最低评分=").append(dto.getMinScore()).append("\n");
        sb.append("老师列表：\n");

        for (Teacher t : teachers) {
            sb.append("ID: ").append(t.getTeacherId())
                    .append(", 评分: ").append(t.getScore())
                    .append(", 教育: ").append(t.getEducation())
                    .append(", 教授年级: ").append(t.getTeachGrade())
                    .append(", 经验: ").append(t.getExperience())
                    .append(", 爱好: ").append(t.getHobby())
                    .append("\n");
        }

        sb.append("请只返回ID列表，格式如：123,456,789");
        return sb.toString();
    }

    /**
     * 使用 AI 对学生进行排序，返回排序后的 ID 列表
     *
     * @param students 学生列表
     * @param dto      匹配参数
     * @return 排序后的学生 ID 列表
     */
    @Override
    public List<Long> rankStudentsByAi(List<Student> students, MatchStudentDTO dto) {
        String prompt = buildStudentRankingPrompt(dto, students);
        String aiResponse = chat(prompt).trim();
        return parseAiRanking(aiResponse);
    }

    /**
     * 构建对学生进行排序的提示词（prompt）
     *
     * @param dto      匹配参数
     * @param students 学生列表
     * @return 构建好的提示词文本
     */
    private String buildStudentRankingPrompt(MatchStudentDTO dto, List<Student> students) {
        StringBuilder sb = new StringBuilder();
        sb.append("请根据教师需求和学生信息，按照匹配程度从高到低排序学生，并返回学生ID列表，逗号分隔。\n");
        sb.append("教师需求：科目=").append(dto.getSubject())
                .append(", 年级=").append(dto.getGrade())
                .append(", 最低评分=").append(dto.getMinScore()).append("\n");
        sb.append("学生列表：\n");

        for (Student s : students) {
            sb.append("ID: ").append(s.getStudentId())
                    .append(", 评分: ").append(s.getScore())
                    .append(", 年级: ").append(s.getGrade())
                    .append(", 科目: ").append(s.getSubject())
                    .append(", 目标: ").append(s.getGoal())
                    .append(", 爱好: ").append(s.getHobby())
                    .append("\n");
        }

        sb.append("请只返回ID列表，格式如：123,456,789");
        return sb.toString();
    }

    /**
     * 解析 AI 返回的字符串，转换为 ID 列表
     *
     * @param aiResponse AI 返回的结果字符串
     * @return 解析后的 Long 列表
     */
    private List<Long> parseAiRanking(String aiResponse) {
        return Arrays.stream(aiResponse.trim().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 异步调用 AI 获取用户数据并保存
     *
     * @param userId 用户 ID
     */
    @Override
    @Async
    public void fetchAiData(Long userId) {
        String apiKey = "sk-2cdaa628f72e496e9bd19ab75f9afb6a";
        String baseUrl = "https://api.deepseek.com";

        String requestBody = "{"
                + "\"model\": \"deepseek-chat\","
                + " \"messages\": ["
                + "     {"
                + "         \"role\": \"system\","
                + "         \"content\": \"You are a helpful assistant.\""
                + "     },"
                + "     {"
                + "         \"role\": \"user\","
                + "         \"content\": \"" + "我如何选择一个合适的老师" + "\""
                + "     }"
                + " ],"
                + "\"stream\": false"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String apiUrl = baseUrl + "/chat/completions";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        String responseBody = responseEntity.getBody();
        log.info("用户{}的AI数据已获取", userId);
        if (responseBody != null) {
            log.info(responseBody);
        }
    }
}