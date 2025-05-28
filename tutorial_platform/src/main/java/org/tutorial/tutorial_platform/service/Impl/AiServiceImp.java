package org.tutorial.tutorial_platform.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tutorial.tutorial_platform.service.AiService;

@Service
@RequiredArgsConstructor
public class AiServiceImp implements AiService {

    private final RestTemplate restTemplate;

    @Override
    public String chat(String question) {
        // DeepSeek API 配置
        String apiKey = "sk-2cdaa628f72e496e9bd19ab75f9afb6a";
        String baseUrl = "https://api.deepseek.com"; // 确保 URL 是有效的

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
        headers.set("Authorization", "Bearer " + apiKey); // 添加 API 密钥

        // 创建 HttpEntity 对象
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 构造完整的 API URL
        String apiUrl = baseUrl + "/chat/completions"; // 指定完整的 API 端点

        // 发起同步请求并获取响应
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                apiUrl,
                requestEntity,
                String.class
        );

        // 获取响应体
        return responseEntity.getBody();
    }
}