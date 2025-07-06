package org.tutorial.tutorial_platform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tutorial.tutorial_platform.dto.AiRequestDTO;
import org.tutorial.tutorial_platform.dto.LoginDTO;

@SpringBootTest(classes = org.tutorial.tutorial_platform.TutorialPlatformApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String TOKEN;
    private static String STUDENT_TOKEN;
    private static String TEACHER_TOKEN;

    @BeforeAll
    static void beforeAll() {
        TOKEN = null;
        STUDENT_TOKEN = null;
        TEACHER_TOKEN = null;
    }

    @Test
    @Order(1)
    @DisplayName("登录并保存token")
    void loginAndSaveToken() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("user1");
        dto.setPassword("123456");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(body);
        TOKEN = jsonNode.get("token").asText();
        Assertions.assertNotNull(TOKEN, "登录响应未返回Token");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/deepseek/ask - 基础测试")
    void testAskQuestion() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/deepseek/ask")
                .param("question", "你好，AI！")
                .header("Token", TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/deepseek/generate - 基础测试")
    void testGenerateFromJson() throws Exception {
        AiRequestDTO dto = new AiRequestDTO();
        dto.setPrompt("请介绍一下AI");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/deepseek/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Token", TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/deepseek/fetch-ai-data - 学生身份测试")
    void testFetchAiDataByStudent() throws Exception {
        if (STUDENT_TOKEN == null) {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("user1");
            dto.setPassword("123456");
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
            String body = result.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(body);
            STUDENT_TOKEN = jsonNode.get("token").asText();
            Assertions.assertNotNull(STUDENT_TOKEN, "学生登录未返回Token");
        }
        mockMvc.perform(MockMvcRequestBuilders.get("/api/deepseek/fetch-ai-data")
                .header("Token", STUDENT_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/deepseek/fetch-ai-data - 老师身份测试")
    void testFetchAiDataByTeacher() throws Exception {
        if (TEACHER_TOKEN == null) {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("user2");
            dto.setPassword("123456");
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
            String body = result.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(body);
            TEACHER_TOKEN = jsonNode.get("token").asText();
            Assertions.assertNotNull(TEACHER_TOKEN, "老师登录未返回Token");
        }
        mockMvc.perform(MockMvcRequestBuilders.get("/api/deepseek/fetch-ai-data")
                .header("Token", TEACHER_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(99)
    @DisplayName("登录密码错误应失败")
    void loginWithWrongPassword() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("user1");
        dto.setPassword("wrongpassword");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }
}