package org.tutorial.tutorial_platform.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tutorial.tutorial_platform.dto.ChatMessageDTO;
import org.tutorial.tutorial_platform.pojo.*;
import org.tutorial.tutorial_platform.repository.*;
import org.tutorial.tutorial_platform.service.ChatService;
import org.tutorial.tutorial_platform.vo.ChatMessageVO;
import org.tutorial.tutorial_platform.vo.ChatSessionVO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * ChatServiceImpl - 聊天服务实现类
 * 实现 ChatService 接口定义的核心聊天业务逻辑
 *
 * 依赖组件：
 * - ChatSessionRepository 聊天会话数据访问接口
 * - ChatMessageRepository 聊天消息数据访问接口
 * - UserSessionMappingRepository 用户会话映射数据访问接口
 * - UserTotalUnreadRepository 用户未读消息汇总数据访问接口
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-20
 */
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;  // 用于查询当前用户是否属于老师

    @Autowired
    private StudentRepository studentRepository;  // 用于查询当前用户是否属于学生

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserSessionMappingRepository userSessionMappingRepository;
    @Autowired
    private UserTotalUnreadRepository userTotalUnreadRepository;

    // 创建新会话（如果不存在）
    @Override
    @Transactional
    public ChatSessionVO createAndEnterSession(Long userId1, Long userId2) {
        // 0. 检查是不是自己给自己发消息
        if(userId1.equals(userId2)) {
            throw new RuntimeException("不能自己给自己发消息");
        }
        // 1. 检查是否已有会话,如果有则进入会话并将用户对应会话的未读消息置0，同时减少用户总未读消息数
        Optional<ChatSession> existingSession = chatSessionRepository.findByUserIds(userId1, userId2);
        if (existingSession.isPresent()) {
            userSessionMappingRepository.findByUserIdAndSessionId(userId1, existingSession.get().getSessionId())
                    .ifPresent(mapping -> {
                        Integer unreadCount = mapping.getUnreadCount();
                        if(unreadCount > 0) {
                            userTotalUnreadRepository.decrementTotalUnread(userId1, unreadCount);
                            mapping.setUnreadCount(0);
                            userSessionMappingRepository.save(mapping);
                        }
                    });

            return new ChatSessionVO(existingSession.get(),teacherRepository,studentRepository); // 返回已有会话
        }
        // 2. 判断user1身份
        boolean isUser1Teacher = teacherRepository.existsByUserUserId(userId1);

        // 3. 创建新会话并保存
        ChatSession newSession = new ChatSession();
        newSession.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        if (isUser1Teacher) {
            // userId1是老师，userId2是学生
            newSession.setTeacherUserId(userId1);
            newSession.setStudentUserId(userId2);
        } else {
            // userId1是学生，userId2是老师
            newSession.setTeacherUserId(userId2);
            newSession.setStudentUserId(userId1);
        }
        ChatSession savedSession = chatSessionRepository.save(newSession);

        // 4. 创建userSessionMapping对象并保存会话到UserSessionMapping表
        UserSessionMapping userSessionMapping1 = new UserSessionMapping();
        userSessionMapping1.setUserId(userId1);
        userSessionMapping1.setChatSession(savedSession);  // 这里设置session_id要这样设置！
        userSessionMapping1.setUnreadCount(0);
        userSessionMappingRepository.save(userSessionMapping1);
        UserSessionMapping userSessionMapping2 = new UserSessionMapping();
        userSessionMapping2.setUserId(userId2);
        userSessionMapping2.setChatSession(savedSession);  // 这里设置session_id要这样设置！
        userSessionMapping2.setUnreadCount(0);
        userSessionMappingRepository.save(userSessionMapping2);

        // 5. 返回VO对象
        return new ChatSessionVO(savedSession,teacherRepository,studentRepository);
    }

    @Override
    public void updateSessionLastMessage(Long sessionId, String content, LocalDateTime lastMessageTime) {
        chatSessionRepository.findById(sessionId) // 返回Optional<Session>
                .ifPresent(session -> {           // 如果存在，执行lambda表达式，并会解包optional<session>
                    // 在这里可以直接使用session对象调用方法
                    session.setLastMessageContent(content);
                    session.setLastMessageTime(lastMessageTime);
                    chatSessionRepository.save(session);
                });
    }

    @Override
    public Page<ChatSessionVO> getUserSessions(Long curUserId, Pageable pageable) {
        // 1. 分页查询用户会话映射（包含关联的ChatSession）
        Page<UserSessionMapping> mappings = userSessionMappingRepository
                .findUserSessionsOrderByLastMessageTime(curUserId, pageable);

        // 2. 转换为VO（包含教师/学生姓名和未读计数）
        List<ChatSessionVO> vos = mappings.getContent().stream()
                .map(mapping -> {
                    // 获取关联的ChatSession（通过LAZY加载）
                    ChatSession session = mapping.getChatSession();
                    // 创建VO并设置未读计数
                    return new ChatSessionVO(
                            session,
                            teacherRepository,
                            studentRepository,
                            mapping.getUnreadCount()
                    );
                }).toList();

        // 3. 构造分页响应
        return new PageImpl<>(
                vos,
                pageable,
                mappings.getTotalElements()
        );
    }

    @Override
    @Transactional
    public void deleteSession(Integer sessionId,Long curUserId,String userType) {
        Long longSessionId = sessionId.longValue();
        // 1. 检查有效性
        Optional<ChatSession> existingSession = userType.equals("STUDENT") ?
                chatSessionRepository.findBySessionIdAndStudentUserId(longSessionId, curUserId):
                chatSessionRepository.findBySessionIdAndTeacherUserId(longSessionId, curUserId);
        if(existingSession.isEmpty()) {
            throw new RuntimeException("当前用户并未参与此对话，无权删除");
        }
        // 2. 删除会话
        chatSessionRepository.deleteSessionBySessionId(longSessionId);
        // 3. 删除与会话有关的所有消息
        chatMessageRepository.deleteMessageBySessionId(longSessionId);
        // 4. 获取两个用户会话关联信息，方便更新用户未读消息数
        List<UserSessionMapping> mappings = userSessionMappingRepository.findAllBySessionId(longSessionId);
        if(mappings.isEmpty()) {
            throw new RuntimeException("查找不到用户与会话的关联");
        }
        else if(mappings.size() != 2){
            throw new RuntimeException("与会话关联的用户必须要是两个");
        }
        UserSessionMapping mapping1 = mappings.get(0);
        UserSessionMapping mapping2 = mappings.get(1);
        // 5. 删除双方用户和会话的映射关系
        userSessionMappingRepository.deleteMappingBySessionId(longSessionId);
        // 6. 更新用户所有未读消息数
        if(mapping1.getUnreadCount() > 0){
            userTotalUnreadRepository.decrementTotalUnread(mapping1.getUserId(), mapping1.getUnreadCount());
        }
        if(mapping2.getUnreadCount() > 0){
            userTotalUnreadRepository.decrementTotalUnread(mapping2.getUserId(), mapping2.getUnreadCount());
        }
    }


    // 发送消息
    @Override
    @Transactional
    public ChatMessageVO sendMessage(ChatMessageDTO messageDTO, Long curUserId) {
        // 1. 验证发送者是否为当前用户（防止身份伪造）
        if(!messageDTO.getSenderId().equals(curUserId)) {
            throw new RuntimeException("发送消息者与当前用户Id不一致");
        }
        // 2. 保存消息
        ChatMessage message = new ChatMessage();
        message.setSessionId(messageDTO.getSessionId());
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 3. 更新会话的最后消息和时间
        String lastContent = messageDTO.getContent();
        if (lastContent != null && lastContent.length() > 15) {
            lastContent = lastContent.substring(0, 15); // 截取前15个字符
        }
        updateSessionLastMessage(messageDTO.getSessionId(),lastContent,message.getCreateTime());

        // 4. 将userSessionMapping表和UserTotalUnread表中对方的未读消息数+1
        Optional<UserSessionMapping> existingMapping = userSessionMappingRepository.findByUserIdAndSessionId(messageDTO.getReceiverId(),messageDTO.getSessionId());
        if(existingMapping.isPresent()) {
            UserSessionMapping mapping = existingMapping.get();
            mapping.setUnreadCount(mapping.getUnreadCount() + 1);
            userSessionMappingRepository.save(mapping);
            userTotalUnreadRepository.incrementTotalUnread(messageDTO.getReceiverId());
        }

        // 5. 转换为VO返回
        return new ChatMessageVO(savedMessage,curUserId,teacherRepository,studentRepository);
    }

   // 获取会话历史消息（分页）,分页参数由前端来传
    @Override
    @Transactional
    public Page<ChatMessageVO> getSessionMessages(Long sessionId, Long curUserId, Pageable pageable) {
        // 1. 每次获取会话全部信息都将处理一次未读消息数，方便双方互相对话时的情景
        userSessionMappingRepository.findByUserIdAndSessionId(curUserId,sessionId)
                .ifPresent(usm -> {
                    Integer unreadCount = usm.getUnreadCount();
                    if(unreadCount > 0){
                        userTotalUnreadRepository.decrementTotalUnread(curUserId,unreadCount);
                        usm.setUnreadCount(0);
                        userSessionMappingRepository.save(usm);
                    }
                });
        // 2. 从数据库分页查询原始消息，获取到的是实体类ChatMessage
        Page<ChatMessage> messagePage = chatMessageRepository.findBySessionIdOrderByCreateTimeDesc(sessionId, pageable);
        // 3. 转换为VO并设置消息位置（left/right）,对Page中的每个ChatMessage对象调用转换函数。
        return messagePage.map(message -> new ChatMessageVO(message, curUserId, teacherRepository, studentRepository));
    }

    @Override
    public Integer getUserUnreadCount(Long curUserId) {
        Optional<UserTotalUnread> uus = userTotalUnreadRepository.findByUserId(curUserId);
        if(uus.isPresent()) {
            return uus.get().getTotalUnread();
        }
        else{
            throw new RuntimeException("未查找到当前用户");
        }
    }


}