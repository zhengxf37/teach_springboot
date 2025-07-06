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

@SpringBootTest(classes = org.tutorial.tutorial_platform.TutorialPlatformApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String XIAOMING_TOKEN;
    private static String DAXUESHENG_TOKEN;
    private static Long SESSION_ID;
    private static Long XIAOMING_ID;
    private static Long DAXUESHENG_ID;

    @BeforeAll
    static void initTokens(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // 登录小明
        LoginDTO xmLogin = new LoginDTO();
        xmLogin.setUsername("user1");
        xmLogin.setPassword("123456");
        MvcResult xmResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xmLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String xmBody = xmResult.getResponse().getContentAsString();
        JsonNode xmNode = objectMapper.readTree(xmBody);
        XIAOMING_TOKEN = xmNode.get("token").asText();
        XIAOMING_ID = xmNode.get("userId").asLong();

        // 登录大学生
        LoginDTO dxsLogin = new LoginDTO();
        dxsLogin.setUsername("user2");
        dxsLogin.setPassword("123456");
        MvcResult dxsResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dxsLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String dxsBody = dxsResult.getResponse().getContentAsString();
        JsonNode dxsNode = objectMapper.readTree(dxsBody);
        DAXUESHENG_TOKEN = dxsNode.get("token").asText();
        DAXUESHENG_ID = dxsNode.get("userId").asLong();
    }

    @BeforeEach
    void createSession() throws Exception {
        // 每次测试前都新建会话，获取sessionId
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/chat/sessions")
                .param("targetUserId", String.valueOf(DAXUESHENG_ID))
                .header("Token", XIAOMING_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(body);
        SESSION_ID = node.get("sessionId").asLong();
    }
//
//    @Test
//    @Order(1)
//    @DisplayName("创建/进入会话")
//    void testCreateOrEnterSession() {
//        // 已在@BeforeEach中测试，无需重复断言
//    }

    @Test
    @Order(2)
    @DisplayName("发送消息（小明向大学生）")
    void testSendMessage() throws Exception {
        String json = String.format("{\"sessionId\":%d,\"senderId\":%d,\"receiverId\":%d,\"content\":\"老师，我要学编程\"}", SESSION_ID, XIAOMING_ID, DAXUESHENG_ID);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chat/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", XIAOMING_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("获取会话消息历史")
    void testGetSessionMessages() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chat/messages")
                .param("sessionId", String.valueOf(SESSION_ID))
                .param("page", "0")
                .param("size", "10")
                .header("Token", XIAOMING_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("获取用户会话列表（大学生）")
    void testGetUserSessions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chat/sessions")
                .param("page", "0")
                .param("size", "10")
                .header("Token", DAXUESHENG_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("获取当前用户所有未读消息数（大学生）")
    void testGetUnreadMsgCount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chat/UnreadMsgCount")
                .header("Token", DAXUESHENG_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(6)
    @DisplayName("删除会话")
    void testDeleteSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/chat/deleteSession")
                .content(String.valueOf(SESSION_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Token", DAXUESHENG_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("发送消息-内容为空应失败")
    void testSendMessage_EmptyContent() throws Exception {
        String json = String.format("{\"sessionId\":%d,\"senderId\":%d,\"receiverId\":%d,\"content\":\"\"}", SESSION_ID, XIAOMING_ID, DAXUESHENG_ID);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chat/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", XIAOMING_TOKEN))
                .andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("未登录获取会话应失败")
    void testGetSessions_NoToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chat/sessions")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("删除不存在的会话应失败")
    void testDeleteSession_NotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/chat/deleteSession")
                .content("99999999")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Token", DAXUESHENG_TOKEN))
                .andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }


    @Test
    @DisplayName("自己给自己发消息应失败")
    void testSendMessage_ToSelf() throws Exception {
        String json = String.format("{\"sessionId\":%d,\"senderId\":%d,\"receiverId\":%d,\"content\":\"自言自语\"}", SESSION_ID, XIAOMING_ID, XIAOMING_ID);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chat/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Token", XIAOMING_TOKEN))
                .andExpect(result -> Assertions.assertNotEquals(200, result.getResponse().getStatus()));
    }

} 