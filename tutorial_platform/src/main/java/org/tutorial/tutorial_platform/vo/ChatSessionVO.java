package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.ChatSession;
import org.tutorial.tutorial_platform.pojo.Teacher;
import org.tutorial.tutorial_platform.pojo.Student;
import org.tutorial.tutorial_platform.repository.StudentRepository;
import org.tutorial.tutorial_platform.repository.TeacherRepository;

import java.time.LocalDateTime;

@Data
public class ChatSessionVO {
    private Long sessionId;
    private Long teacherUserId;
    private Long studentUserId;
    private String teacherName;
    private String studentName;
    private String createTime;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Integer unreadMsgCount;  // 针对当前用户返回的未读消息数
    // 构造函数
    public ChatSessionVO(ChatSession session,
                         TeacherRepository teacherRepository,
                         StudentRepository studentRepository) {
        this(session,teacherRepository,studentRepository,0);
    }
    public ChatSessionVO(ChatSession session,
                         TeacherRepository teacherRepository,
                         StudentRepository studentRepository,
                         Integer unreadMsgCount) {
        this.sessionId = session.getSessionId();
        this.teacherUserId = session.getTeacherUserId();
        this.studentUserId = session.getStudentUserId();
        this.createTime = formatLocalDateTime(session.getCreateTime()); // 调用格式化方法
        this.lastMessageContent = session.getLastMessageContent();
        this.lastMessageTime = session.getLastMessageTime();
        this.unreadMsgCount = unreadMsgCount;
        // 从教师仓库获取教师姓名
        this.teacherName = teacherRepository.findByUserUserId(session.getTeacherUserId())
                .map(Teacher::getName)    // 从optional<Teacher>中自动解包（如果存在），然后从Teacher中提取name
                .orElse("未知教师");       // 如果任何一步为null，返回默认值

        // 从学生仓库获取学生姓名
        this.studentName = studentRepository.findByUserUserId(session.getStudentUserId())
                .map(Student::getName)  // 从optional<Student>中自动解包（如果存在），然后从Student中提取name
                .orElse("未知学生");  // 如果任何一步为null，返回默认值
    }
    // 格式化 LocalDateTime 为字符串（例如："2023-01-01 12:00"）
    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.toString().replace("T", " ").substring(0, 16);
    }
} 