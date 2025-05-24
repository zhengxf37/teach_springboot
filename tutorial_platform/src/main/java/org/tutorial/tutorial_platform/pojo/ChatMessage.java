package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * ChatMessage - 聊天消息实体类
 * 与数据库表 chat_message 对应
 *
 * 元信息：
 * @author zxf
 */
@Data
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}