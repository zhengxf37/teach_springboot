package org.tutorial.tutorial_platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tutorial.tutorial_platform.dto.ChatMessageDTO;
import org.tutorial.tutorial_platform.service.ChatService;
import org.tutorial.tutorial_platform.vo.ChatMessageVO;
import org.tutorial.tutorial_platform.vo.ChatSessionVO;

/**
 * ChatController - 聊天控制器
 * 处理所有与聊天相关的 HTTP 请求，包括会话管理、消息管理和未读消息管理
 *
 * 元信息：
 * @author zxf
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 创建或进入私聊会话
     * @param request      包含从JWT解析的当前用户信息
     * @param targetUserId 目标用户ID（必须传）
     * @return 会话信息
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionVO> createOrEnterPrivateSession(
            HttpServletRequest request,
            @RequestBody Long targetUserId) {
        // 1. 从认证信息中获取当前用户
        Long currentUserId = (Long) request.getAttribute("userId");
        // 2. 调用服务层处理
        ChatSessionVO session = chatService.createAndEnterSession(currentUserId, targetUserId);
        // 3. 返回结果
        return ResponseEntity.ok(session);
    }

    /**
     * 获取用户的会话列表
     * @param request 网络请求
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<Page<ChatSessionVO>> getUserSessions(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,      // 页码，默认为0
            @RequestParam(defaultValue = "10") int size) {   // 每页条数，默认为10
        // 1. 获取当前用户ID
        Long curUserId = (Long) request.getAttribute("userId");
        // 2. 构建分页参数
        Pageable pageable = PageRequest.of(page, size);
        // 3. 调用服务层
        return ResponseEntity.ok(chatService.getUserSessions(curUserId, pageable));
    }


    /**
     * 发送消息
     * @param request 网络请求
     * @param messageDTO 消息数据
     * @return 发送的消息
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageVO> sendMessage(
            HttpServletRequest request,
            @Valid @RequestBody ChatMessageDTO messageDTO) {
        // 1. 从拦截器设置的request属性中获取当前用户ID
        Long curUserId = (Long) request.getAttribute("userId");
        // 2. 调用服务层发送消息
        ChatMessageVO sentMessage = chatService.sendMessage(messageDTO,curUserId);
        // 3. 返回响应结果
        return ResponseEntity.ok(sentMessage);
    }


    /**
     * 获取会话消息历史
     * @param request 网络请求
     * @return 消息列表
     */
    @GetMapping("/messages")
    public ResponseEntity<Page<ChatMessageVO>> getSessionMessages(
            HttpServletRequest request,
            @RequestParam Long sessionId,          // 会话ID
            @RequestParam(defaultValue = "0") int page,   // 页码，默认0
            @RequestParam(defaultValue = "10") int size   // 每页条数，默认10
    ) {
        // 1. 获取当前用户ID
        Long curUserId = (Long) request.getAttribute("userId");
        // 2. 构建分页请求（按时间倒序）
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createTime").descending()
        );
        // 3. 调用服务层
        Page<ChatMessageVO> messages = chatService.getSessionMessages(
                sessionId,
                curUserId,
                pageable
        );
        return ResponseEntity.ok(messages);
    }


    /**
     * 获取当前用户所有未读消息
     * @param request 网络请求
     * @return 当前用户未读消息数
     */
    @GetMapping("/UnreadMsgCount")
    public ResponseEntity<Integer> getUserUnreadCount(HttpServletRequest request) {
        Long curUserId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(chatService.getUserUnreadCount(curUserId));
    }


    /**
     * 删除会话
     * @param request 网络请求
     * @param sessionId 要删除的会话Id
     */
    @DeleteMapping("/deleteSession")
    public void deleteSession(HttpServletRequest request,@RequestBody Integer sessionId) {
        Long curUserId = (Long) request.getAttribute("userId");
        String userType = (String) request.getAttribute("userType");
        chatService.deleteSession(sessionId,curUserId,userType);
    }

}