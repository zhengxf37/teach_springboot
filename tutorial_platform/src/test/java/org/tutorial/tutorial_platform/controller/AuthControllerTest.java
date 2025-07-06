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
import org.tutorial.tutorial_platform.dto.LoginDTO;
import org.tutorial.tutorial_platform.dto.RegisterDTO;
import org.tutorial.tutorial_platform.pojo.UserType;

@SpringBootTest(classes = org.tutorial.tutorial_platform.TutorialPlatformApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public static String TOKEN = null;

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/login - 登录并保存token")
    void testLoginAndSaveToken() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("user1");
        dto.setPassword("123456");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(MockMvcResultMatchers.status().isOk())
         .andReturn();
        // 从响应体获取Token
        String body = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(body);
        String token = jsonNode.has("token") ? jsonNode.get("token").asText() : null;
        Assertions.assertNotNull(token, "登录响应未返回Token");
        TOKEN = token;
        System.out.println("登录成功，Token=" + TOKEN);
    }

    @Test
    @DisplayName("POST /api/auth/register - 基础测试")
    void testRegister() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        String unique = String.valueOf(System.currentTimeMillis()).substring(7);
        dto.setUsername("tu" + unique);
        dto.setPassword("TestPass123!@#");
        dto.setEmail("test" + unique + "@example.com");
        dto.setUserType(UserType.valueOf("STUDENT"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
} 