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
import org.tutorial.tutorial_platform.dto.JudgeUserDTO;
import org.tutorial.tutorial_platform.dto.LoginDTO;

@SpringBootTest(classes = org.tutorial.tutorial_platform.TutorialPlatformApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserInteractionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String TOKEN;
    private static String TEACHER_TOKEN;

    @BeforeAll
    static void beforeAll() { TOKEN = null; }

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
        TOKEN = jsonNode.has("token") ? jsonNode.get("token").asText() : null;
        Assertions.assertNotNull(TOKEN, "登录响应未返回Token");
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/interaction/judge - 评价用户")
    void testJudgeUser() throws Exception {
        Assertions.assertNotNull(TOKEN, "Token未获取");
        JudgeUserDTO judgeUserDTO = new JudgeUserDTO();
        judgeUserDTO.setUserId(1L); // 当前用户id
        judgeUserDTO.setJudgeId(2L); // 被评价用户id
        judgeUserDTO.setContent("测试评价");
//        judgeUserDTO.setScore(5);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/interaction/judge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(judgeUserDTO))
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        judgeUserDTO.setContent("测试修改评价");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/interaction/judge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(judgeUserDTO))
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/interaction/queryjudge - 查询评价")
    void testQueryJudge() throws Exception {
        Assertions.assertNotNull(TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/interaction/queryjudge")
                .param("id", "2")
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/interaction/queryjudge")
                .param("id", "-1")
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(10)
    @DisplayName("老师评价学生")
    void teacherJudgeUser() throws Exception {
        // 假设老师账号 user2 已存在并已登录
        if (TEACHER_TOKEN == null) {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("user2");
            dto.setPassword("123456");
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            ).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()).andReturn();
            String body = result.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(body);
            TEACHER_TOKEN = jsonNode.get("token").asText();
            Assertions.assertNotNull(TEACHER_TOKEN, "老师登录未返回Token");
        }
        JudgeUserDTO judgeUserDTO = new JudgeUserDTO();
        judgeUserDTO.setUserId(2L); // 老师id
        judgeUserDTO.setJudgeId(1L); // 被评价用户id（假设为学生）
        judgeUserDTO.setContent("老师对学生的评价");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/interaction/judge")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(judgeUserDTO))
                .header("Token", TEACHER_TOKEN)
        ).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    }
}