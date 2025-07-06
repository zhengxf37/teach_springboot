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
class UserInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String STUDENT_TOKEN;
    private static String TEACHER_TOKEN;
    private static String NEW_USER_TOKEN;
    private static Long NEW_USER_ID;

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
    @DisplayName("GET /api/user/info/student - 学生信息（学生账号）")
    void testGetStudentInfoByStudent() throws Exception {
        Assertions.assertNotNull(STUDENT_TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/info/student")
                .header("Token", STUDENT_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/user/info/teacher - 教师信息（老师账号）")
    void testGetTeacherInfoByTeacher() throws Exception {
        Assertions.assertNotNull(TEACHER_TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/info/teacher")
                .header("Token", TEACHER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/user/info/user - 通用用户信息（学生账号）")
    void testGetUserInfoByStudent() throws Exception {
        Assertions.assertNotNull(STUDENT_TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/info/user")
                .header("Token", STUDENT_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @Order(6)
    @DisplayName("GET /api/user/info/user - 通用用户信息（老师账号）")
    void testGetUserInfoByTeacher() throws Exception {
        Assertions.assertNotNull(TEACHER_TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/info/user")
                .header("Token", TEACHER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(100)
    @DisplayName("新用户登录并保存token和userId")
    void loginNewUserAndSaveToken() throws Exception {
        // 假设新用户已注册
        LoginDTO dto = new LoginDTO();
        dto.setUsername("user11");
        dto.setPassword("123456");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(body);
        NEW_USER_TOKEN = node.get("token").asText();
        NEW_USER_ID = node.get("userId").asLong();
        Assertions.assertNotNull(NEW_USER_TOKEN);
        Assertions.assertNotNull(NEW_USER_ID);
    }

    @Test
    @Order(101)
    @DisplayName("修改新用户通用信息")
    void updateNewUserInfo() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String updateJson = "{\"userId\":-1,\"username\":\"newuser_new1\",\"password\":\"123456\",\"email\":\"newuser61@qq.com\",\"userType\":\"STUDENT\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/user")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(updateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(102)
    @DisplayName("查询新用户通用信息")
    void getNewUserInfo() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/info/user")
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(103)
    @DisplayName("修改新用户学生属性")
    void updateNewUserStudentInfo() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String stuUpdateJson = String.format("{" +
                "\"userId\":-1," +
                "\"name\":\"学生张三\"," +
                "\"gender\":\"MALE\"," +
                "\"grade\":\"高中\"," +
                "\"subject\":\"数学\"," +
                "\"address\":\"北京市朝阳区\"," +
                "\"phone\":\"13800000000\"," +
                "\"score\":95.5," +
                "\"hobby\":\"篮球\"," +
                "\"goal\":\"考上好大学\"," +
                "\"addition\":\"无\"}" );
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/student")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(stuUpdateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(1031)
    @DisplayName("修改新用户老师属性")
    void updateNewUserTeacherInfo() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String teacherUpdateJson = String.format("{" +
                "\"userId\":-1," +
                "\"name\":\"教师李四\"," +
                "\"gender\":\"FEMALE\"," +
                "\"education\":\"硕士\"," +
                "\"teachGrade\":\"高三\"," +
                "\"subject\":\"英语\"," +
                "\"address\":\"上海市浦东新区\"," +
                "\"phone\":\"13900000000\"," +
                "\"experience\":5," +
                "\"score\":98.0," +
                "\"hobby\":\"阅读\"," +
                "\"school\":\"复旦大学\"," +
                "\"addition\":\"有海外留学经验\"}" );
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/teacher")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(teacherUpdateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(104)
    @DisplayName("再次修改新用户通用信息")
    void updateNewUserInfoAgain() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String updateJson2 = "{\"userId\":-1,\"username\":\"user11\",\"password\":\"123456\",\"email\":\"newuser62@qq.com\",\"userType\":\"TEACHER\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/user")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(updateJson2)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/user/update/user - 缺少参数应失败")
    void updateUserInfo_MissingParam() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String updateJson = "{}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/user")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(updateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("POST /api/user/update/user - 邮箱格式错误应失败")
    void updateUserInfo_EmailFormatError() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String updateJson = "{\"userId\":-1,\"username\":\"testuser\",\"password\":\"123456\",\"email\":\"not-an-email\",\"userType\":\"STUDENT\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/user")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(updateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("POST /api/user/update/student - 缺少参数应失败")
    void updateStudentInfo_MissingParam() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String stuUpdateJson = "{}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/student")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(stuUpdateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("POST /api/user/update/teacher - 缺少参数应失败")
    void updateTeacherInfo_MissingParam() throws Exception {
        Assertions.assertNotNull(NEW_USER_TOKEN);
        String teacherUpdateJson = "{}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/teacher")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(teacherUpdateJson)
                .header("Token", NEW_USER_TOKEN)
        ).andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }
}

