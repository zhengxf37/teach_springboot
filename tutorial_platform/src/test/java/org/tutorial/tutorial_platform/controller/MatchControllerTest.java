package org.tutorial.tutorial_platform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tutorial.tutorial_platform.dto.LoginDTO;

@SpringBootTest(classes = org.tutorial.tutorial_platform.TutorialPlatformApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String STUDENT_TOKEN;
    private static String TEACHER_TOKEN;

    @BeforeAll
    static void beforeAll() { STUDENT_TOKEN = null; TEACHER_TOKEN = null; }

    @Test
    @Order(1)
    @DisplayName("学生登录并保存token")
    void loginStudentAndSaveToken() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("user1");
        dto.setPassword("123456");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(body);
        STUDENT_TOKEN = jsonNode.get("token").asText();
        Assertions.assertNotNull(STUDENT_TOKEN, "学生登录未返回Token");
    }

    @Test
    @Order(2)
    @DisplayName("老师登录并保存token")
    void loginTeacherAndSaveToken() throws Exception {
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

    @Test
    @Order(3)
    @DisplayName("POST /api/match/teachers/ai - 学生找老师（学生账号）")
    void testFindTeachersWithAiByStudent() throws Exception {
        Assertions.assertNotNull(STUDENT_TOKEN, "Token未获取");
        String json = "{\"userId\":1,\"subject\":\"数学\",\"grade\":\"高中\",\"score\":0}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/match/teachers/ai")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", STUDENT_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @Order(4)
    @DisplayName("POST /api/match/students/ai - 老师找学生（老师账号）")
    void testFindStudentsWithAiByTeacher() throws Exception {
        Assertions.assertNotNull(TEACHER_TOKEN, "Token未获取");
        String json = "{\"userId\":2,\"subject\":\"数学\",\"grade\":\"高中\",\"score\":0}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/match/students/ai")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", TEACHER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/match/save - 保存向量信息（学生账号）")
    void testSaveWithVectorByStudent() throws Exception {
        Assertions.assertNotNull(STUDENT_TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/save")
                .header("Token", STUDENT_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/match/save - 保存向量信息（老师账号）")
    void testSaveWithVectorByTeacher() throws Exception {
        Assertions.assertNotNull(TEACHER_TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/save")
                .header("Token", TEACHER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/match/teachers/ai - 缺少参数应失败")
    void testFindTeachersWithAi_MissingParam() throws Exception {
        Assertions.assertNotNull(STUDENT_TOKEN, "Token未获取");
        String json = "{}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/match/teachers/ai")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", STUDENT_TOKEN)
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

//    @Test
//    @DisplayName("POST /api/match/teachers/ai - 非法分数应失败")
//    void testFindTeachersWithAi_InvalidScore() throws Exception {
//        Assertions.assertNotNull(STUDENT_TOKEN, "Token未获取");
//        String json = "{\"userId\":1,\"subject\":\"数学\",\"grade\":\"高一\",\"score\":-100}";
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/match/teachers/ai")
//                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
//                .content(json)
//                .header("Token", STUDENT_TOKEN)
//        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
//    }

    @Test
    @DisplayName("POST /api/match/students/ai - 缺少参数应失败")
    void testFindStudentsWithAi_MissingParam() throws Exception {
        Assertions.assertNotNull(TEACHER_TOKEN, "Token未获取");
        String json = "{}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/match/students/ai")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", TEACHER_TOKEN)
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("GET /api/match/save - 未登录应失败")
    void testSaveWithVector_NoToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/match/save"))
                .andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }
} 