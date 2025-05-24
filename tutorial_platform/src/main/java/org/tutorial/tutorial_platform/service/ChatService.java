package org.tutorial.tutorial_platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tutorial.tutorial_platform.dto.ChatMessageDTO;
import org.tutorial.tutorial_platform.vo.ChatMessageVO;
import org.tutorial.tutorial_platform.vo.ChatSessionVO;

import java.time.LocalDateTime;

/**
 * ChatService - 聊天服务接口
 * 提供聊天相关的核心业务逻辑，包括会话管理、消息管理和未读消息管理
 *
 * 安全说明：
 * - 所有方法都需要验证用户身份
 * - 用户只能操作自己的会话和消息
 *
 * 元信息：
 * @author zxf
 */
public interface ChatService {
    // 1. 会话相关
    // （创建）并进入会话
    ChatSessionVO createAndEnterSession(Long userId1, Long userId2);
    // 更新会话最后的消息以及其对应的时间
    void updateSessionLastMessage(Long sessionId, String content, LocalDateTime lastMessageTime);
    // 根据用户ID获取所有会话，分页返回
    Page<ChatSessionVO> getUserSessions(Long curUserId, Pageable pageable);
    // 删除会话，以及删去userSessionMapping和chatMessage中与该会话关联的部分，并且处理用户全部未读消息数
    void deleteSession(Integer sessionId,Long curUserId,String userType);

    // 2. 消息相关
    // 发送消息
    ChatMessageVO sendMessage(ChatMessageDTO messageDTO,Long curUserId);
    // 获取具体会话的所有消息，返回分页结果
    Page<ChatMessageVO> getSessionMessages(Long sessionId, Long curUserId, Pageable pageable);

    // 3. 未读消息模块
    // 获取当前用户未读的所有消息数
    Integer getUserUnreadCount(Long curUserId);

}