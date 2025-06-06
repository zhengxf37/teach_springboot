package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.ChatMessage;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;

import java.time.LocalDateTime;

@Data
public class ChatMessageVO {
    private Long messageId;
    private Long sessionId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private String content;
    private String createTime;
    private String pos; // "left" 或 "right"，用于标识消息显示位置
    // 简单构造函数（从实体对象转换）
    public ChatMessageVO(ChatMessage message, Long curUserId,
                         TeacherRepository teacherRepository,
                         StudentRepository studentRepository) {
        this.messageId = message.getMessageId();
        this.sessionId = message.getSessionId();
        this.senderId = message.getSenderId();
        this.receiverId = message.getReceiverId();
        this.content = message.getContent();
        this.createTime = formatLocalDateTime(message.getCreateTime()); // 调用格式化方法
        this.pos = (message.getSenderId().equals(curUserId)) ? "right" : "left";
        boolean isSenderTeacher = teacherRepository.existsByUserUserId(message.getSenderId());
        if(isSenderTeacher) {
            this.senderName = teacherRepository.findByUserUserId(message.getSenderId()).get().getName();
            this.receiverName = studentRepository.findByUserUserId(message.getReceiverId()).get().getName();
        }
        else{
            this.senderName = studentRepository.findByUserUserId(message.getSenderId()).get().getName();
            this.receiverName = teacherRepository.findByUserUserId(message.getReceiverId()).get().getName();
        }
    }

    // 格式化 LocalDateTime 为字符串（例如："2023-01-01 12:00"）
    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.toString().replace("T", " ").substring(0, 16);
    }
} 