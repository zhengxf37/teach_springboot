package org.tutorial.tutorial_platform.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import org.tutorial.tutorial_platform.pojo.*;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;
import org.tutorial.tutorial_platform.repository.UserCommentRepository;
import org.tutorial.tutorial_platform.repository.UserRepository;
import org.tutorial.tutorial_platform.service.AiService;


/**
 * AI 服务实现类，负责与第三方 AI 接口交互，生成学生和教师的特征向量。
 * 功能说明：
 * - 使用 DeepSeek 的 API 为学生和老师生成匹配向量；
 * - 向量基于：性别、年级、科目、地址、手机号、评分、爱好等字段；
 * - 向量用于本地余弦相似度排序；
 * - 所有 AI 调用都走异步，避免阻塞主线程；
 * - 提供统一接口供 MatchService 调用。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiServiceImp implements AiService {

    // RestTemplate 用于发送 HTTP 请求到 DeepSeek 的 Chat API
    private final RestTemplate restTemplate;

    // Jackson ObjectMapper 用于解析 JSON 响应
    private final ObjectMapper objectMapper;
    //数据库
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final UserCommentRepository userCommentRepository;
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
    public List<Double> getVectorFromAi(Student student) throws RuntimeException, JsonProcessingException {
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
    public List<Double> getVectorFromAi(Teacher teacher) throws RuntimeException, JsonProcessingException {
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
        //TODO更改提示词，优化
        return "请根据以下教师信息生成一个17维数值向量，按顺序对应下列维度，仅输出JSON数组：" +
                "1. 性别特征（男趋向0.0，女趋向1.0）" +
                "2. 教学经验强度（小一趋向0.0，高三趋向1.0）" +
                "3. 理科教学倾向（数理化趋向1.0，纯文科趋向0.0）" +
                "4. 文科教学倾向（语文历史趋向1.0，纯理科趋向0.0）" +
                "5. 实践教学倾向（物理生物趋向1.0，理论科目趋向0.0）" +
                "6. 教学严厉程度（温和趋向1.0，严厉趋向0.0）" +
                "7. 课堂互动能力（内向趋向0.0，活跃趋向1.0）" +
                "8. 教学负责程度（随意趋向0.0，严谨趋向1.0）" +
                "9. 教学方法创新度（传统趋向0.0，创新趋向1.0）" +
                "10. 学生满意度（1分趋向0.0，5分趋向1.0）" +
                "11. 艺术素养（有艺术爱好趋向1.0，无趋向0.0）" +
                "12. 科技素养（有科技爱好趋向1.0，无趋向0.0）" +
                "13. 体育爱好（有趋向1.0，无趋向0.0）" +
                "14. 阅读爱好（有趋向1.0，无趋向0.0）" +
                "15. 社交爱好（有趋向1.0，无趋向0.0）" +
                "16. 情绪稳定性（波动大趋向0.0，稳定趋向1.0）" +
                "17. 开放性（保守趋向0.0，开放趋向1.0）" +
                "18. 区域纬度，对应该地区纬度,大小0-1" +
                "19. 区域经度，对应该地区经度,大小0-1" +

                "以上数字不需要严格为0/1，按照介绍输出0~1的一个小数" +

                "学生信息如下：" +
                "- 性别：" + student.getGender() +
                "- 年级：" + student.getGrade() +
                "- 科目：" + student.getSubject() +
                "- 地址：" + student.getAddress() +
                "- 手机号：" + student.getPhone() +
                "- 评分：" + student.getScore() +
                "- 爱好：" + student.getHobby() +
                "- 目标：" + student.getGoal() +
                "- 补充信息：" + student.getAddition() +

                "输出要求：" +
                "1. 严格输出一个包含19个浮点数的JSON数组，例如：[0.0, 0.48, 1.0, 0.3, 0.8, 0.5, 0.6, 0.9, 0.7, 0.85, 0.7, 1.0, 0.44, 1.0, 0.7, 0.52, 0.68]" +
                "2. 不添加任何额外文字或解释。" +
                "3. 输出必须是合法JSON数组，不能包含其他字符。";
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
        return "请根据以下教师信息生成一个17维数值向量，按顺序对应下列维度，仅输出JSON数组：" +
                "1. 性别特征（男趋向0.0，女趋向1.0）" +
                "2. 教学经验强度（小一趋向0.0，高三趋向1.0）" +
                "3. 理科教学倾向（数理化趋向1.0，纯文科趋向0.0）" +
                "4. 文科教学倾向（语文历史趋向1.0，纯理科趋向0.0）" +
                "5. 实践教学倾向（物理生物趋向1.0，理论科目趋向0.0）" +
                "6. 教学严厉程度（温和趋向1.0，严厉趋向0.0）" +
                "7. 课堂互动能力（内向趋向0.0，活跃趋向1.0）" +
                "8. 教学负责程度（随意趋向0.0，严谨趋向1.0）" +
                "9. 教学方法创新度（传统趋向0.0，创新趋向1.0）" +
                "10. 学生满意度（1分趋向0.0，5分趋向1.0）" +
                "11. 艺术素养（有艺术爱好趋向1.0，无趋向0.0）" +
                "12. 科技素养（有科技爱好趋向1.0，无趋向0.0）" +
                "13. 体育爱好（有趋向1.0，无趋向0.0）" +
                "14. 阅读爱好（有趋向1.0，无趋向0.0）" +
                "15. 社交爱好（有趋向1.0，无趋向0.0）" +
                "16. 情绪稳定性（波动大趋向0.0，稳定趋向1.0）" +
                "17. 开放性（保守趋向0.0，开放趋向1.0）" +
                "18. 区域纬度，对应该地区纬度,大小0-1" +
                "19. 区域经度，对应该地区经度,大小0-1" +

                "以上数字不需要严格为0/1，按照介绍输出0~1的一个小数" +

                "教师信息如下：" +
                "- 性别：" + teacher.getGender() +
                "- 教授年级：" + teacher.getTeachGrade() +
                "- 科目：" + teacher.getSubject() +
                "- 地址：" + teacher.getAddress() +
                "- 手机号：" + teacher.getPhone() +
                "- 评分：" + teacher.getScore() +
                "- 爱好：" + teacher.getHobby() +
                "- 补充信息：" + teacher.getAddition() +

                "输出要求：" +
                "1. 严格输出一个包含19个浮点数的JSON数组，例如：[0.0, 0.48, 1.0, 0.3, 0.8, 0.5, 0.6, 0.9, 0.7, 0.85, 0.7, 1.0, 0.44, 1.0, 0.7, 0.52, 0.68]" +
                "2. 不添加任何额外文字或解释。" +
                "3. 输出必须是合法JSON数组，不能包含其他字符。";
    }

    /**
     * 调用 AI 接口并获取向量结果（同步方法）
     *
     * @param question 包含实体信息的提示词
     * @return 解析后的 Double 向量列表
     */
    private List<Double> callAiAndGetVector(String question) throws RuntimeException, JsonProcessingException {
        //TODO实现prompt
        String vectorStr = chat(question);

        // 校验格式是否为 [ ... ]
        while(!vectorStr.startsWith("[") || !vectorStr.endsWith("]")) {
            log.error("Invalid JSON format: {}", vectorStr);
            vectorStr = chat(question);
        }

        return objectMapper.readValue(vectorStr, new TypeReference<>() {});
    }

    /**
     * 向 DeepSeek 发送请求并获取 AI 的回答
     * 两个实现
     *
     * @param question 用户输入的问题
     * @return AI 返回的回答内容
     */
    public String chat(String question) throws JsonProcessingException {

        return chat(question,"You are a helpful assistant.");
    }
    private static String sanitizeString(String input) {
        if (input == null) return "";
        // 移除 ASCII 控制字符（\u0000 - \u001F）
        return input.replaceAll("[\\u0000-\\u001F]", "");
    }

    public String chat(String question,String prompt) throws JsonProcessingException {
        question = sanitizeString(question);
        prompt = sanitizeString(prompt);
        // 构造请求体
        String requestBody = "{"
                + "\"model\": \"deepseek-chat\","
                + " \"messages\": ["
                + "     {"
                + "         \"role\": \"system\","
                + "         \"content\": \"You are a helpful assistant.\""
                + "     },"
                + "     {"
                + "         \"role\": \"user\","
                + "         \"content\": \"" + question + "\""
                + "     }"
                + " ],"
                + "\"stream\": false"
                + "}";

        // 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY); // 添加 API 密钥

        // 创建 HttpEntity 对象
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 构造完整的 API URL
        String apiUrl = BASE_URL + "/chat/completions"; // 指定完整的 API 端点

        // 发起同步请求并获取响应
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                apiUrl,
                requestEntity,
                String.class
        );

        // 获取响应体
//        return responseEntity.getBody();

        assert responseEntity.getBody() != null;
        String aiResponse = responseEntity.getBody().trim();


        // 使用 Jackson 解析完整 JSON 响应
        JsonNode rootNode = objectMapper.readTree(aiResponse);

        // 提取 content 字段内容（即 AI 返回的向量字符串）
        JsonNode contentNode = rootNode.at("/choices/0/message/content");
        if (contentNode == null || !contentNode.isTextual()) {
            throw new RuntimeException("AI 返回中未找到有效的 content 字段");
        }
        log.info("AI提问：{}\n{}", question,  prompt);
        log.info("AI 响应：{}", contentNode.asText().trim());
        return contentNode.asText().trim();
    }


    /**
     * 异步调用 AI 获取用户数据并保存
     *
     * @param userId 用户 ID
     */
    @Async
    public void fetchAiData(Long userId) throws JsonProcessingException {


        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String s;
        if (user.getUserType() == UserType.STUDENT) {
            Student student = studentRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("学生信息不存在"));
            // 调用 AI 接口获取该学生的评价
            s = comment(student);


        }else if (user.getUserType() == UserType.TEACHER) {

            Teacher teacher = teacherRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("教师信息不存在"));
            // 调用 AI 接口获取表示
            s = comment(teacher);


        }else{
            throw new RuntimeException("用户类型不存在");
        }



        String ans = chat("评价这个人，分析他适合什么样的老师/学生，不用重复他的信息，大概100-200字，只要文字和标点，不要表情包，分点等无关内容",s );//TODO更改提示词

        List<UserComment> existingComment = userCommentRepository.findByUserIdAndFromId(userId, (long) -1);
        UserComment comment;
        if (!existingComment.isEmpty()) {
            comment = existingComment.get(0);
            comment.setContent(ans);
        } else {
            comment = new UserComment(userId, (long) -1, ans);
        }
        userCommentRepository.save(comment);

        log.info("ai保存评价成功id={}",userId);

    }

//    @Override
//    public String askWithData(String question, Long userId, Long anotherId) throws JsonProcessingException {
//        if (userId == null || anotherId == null) {
//            throw new RuntimeException("用户ID不能为空");
//        }
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
//        Student student;
//        Teacher teacher;
//        if (user.getUserType() != UserType.STUDENT){
//            student = studentRepository.findByUserUserId(anotherId).orElseThrow(() -> new RuntimeException("学生信息不存在"));
//            teacher = teacherRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("教师信息不存在"));
//
//        }else{
//            student = studentRepository.findByUserUserId(userId).orElseThrow(() -> new RuntimeException("学生信息不存在"));
//            teacher = teacherRepository.findByUserUserId(anotherId).orElseThrow(() -> new RuntimeException("教师信息不存在"));
//
//        }
//
//        String prompt = comment(student)+comment(teacher);
//        return chat(question,prompt);
//
//
//    }

    private String comment(Student student){
        return "学生信息如下：" +
                "- 姓名：" + student.getName() +
                "- 性别：" + student.getGender() +
                "- 年级：" + student.getGrade() +
                "- 科目：" + student.getSubject() +
                "- 地址：" + student.getAddress() +
                "- 手机号：" + student.getPhone() +
                "- 评分：" +student.getScore() +
                "- 爱好：" + student.getHobby() +
                "- 目标：" + student.getGoal() +
                "- 补充信息：" + student.getAddition();

    }
    private String comment(Teacher teacher){
        return "学生信息如下：" +
                "- 姓名：" + teacher.getName() +
                "- 性别：" + teacher.getGender() +
                "- 年级：" + teacher.getTeachGrade() +
                "- 科目：" + teacher.getSubject() +
                "- 地址：" + teacher.getAddress() +
                "- 手机号：" + teacher.getPhone() +
                "- 评分：" +teacher.getScore() +
                "- 爱好：" + teacher.getHobby() +
                "- 教学经验：" + teacher.getExperience() +
                "- 教学学校：" + teacher.getSchool() +
                "- 补充信息：" + teacher.getAddition();

//TODO 更改提示词
    }
}
