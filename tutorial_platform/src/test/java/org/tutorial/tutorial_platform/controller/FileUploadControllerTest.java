package org.tutorial.tutorial_platform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tutorial.tutorial_platform.dto.LoginDTO;

@SpringBootTest(classes = org.tutorial.tutorial_platform.TutorialPlatformApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileUploadControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String TOKEN;

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
        TOKEN = jsonNode.get("token").asText();
        Assertions.assertNotNull(TOKEN, "登录响应未返回Token");
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/file/uploadfile - 上传文件")
    void testUploadFile() throws Exception {
        Assertions.assertNotNull(TOKEN, "Token未获取");
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/uploadfile")
                .file(file)
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/file/uploadavatar - 上传头像图片")
    void testUploadAvatar() throws Exception {
        Assertions.assertNotNull(TOKEN, "Token未获取");
        MockMultipartFile avatar = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1,2,3,4,5});
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/uploadavatar")
                .file(avatar)
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/file/list - 获取文件列表")
    void testListUserFiles() throws Exception {
        Assertions.assertNotNull(TOKEN, "Token未获取");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/list")
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/file/listavatar - 获取用户头像")
    void testListUserAvatar() throws Exception {
        Assertions.assertNotNull(TOKEN, "Token未获取");
        // userId=-1 代表当前用户
        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/listavatar")
                .param("userId", "-1")
                .header("Token", TOKEN)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
} 