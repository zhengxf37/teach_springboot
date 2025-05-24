package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ChatSession - 聊天会话实体类
 * 与数据库表 chat_session 对应
 * 元信息：
 * @author zxf
 */
@Data
@Entity
@Table(name = "chat_session")
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    // 会话中老师ID
    @Column(name = "teacher_user_id", nullable = false)
    private Long teacherUserId;

    // 会话中学生ID
    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    // 会话创建时间
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // 会话最后一条消息
    @Column(name = "last_message_content", length = 45)
    private String lastMessageContent;

    // 会话最后一条消息发送时间
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;
}