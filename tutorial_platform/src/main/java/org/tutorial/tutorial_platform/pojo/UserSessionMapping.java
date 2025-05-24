package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

/**
 * UserSessionMapping - 用户会话映射实体类
 * 与数据库表 user_session_mapping 对应
 * 元信息：
 * @author zxf
 */
@Data
@Entity
@Table(name = "user_session_mapping")
public class UserSessionMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "unread_count", nullable = false)
    private Integer unreadCount = 0;

    // 只读配置：insertable = false, updatable = false
    // 表示 Hibernate 不会通过这个字段来插入或更新数据库，而是通过关联对象（chatSession）来维护。
    @Column(name = "session_id", insertable = false, updatable = false)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)  // 多对一关联，只有查询时才会加载
    @JoinColumn(name = "session_id", nullable = false)  // 数据库表中用于关联的外键字段名称为sessionId，且对UserMapping表插入和更新时，不会修改SessionId字段
    private ChatSession chatSession;  // 新增关联字段
    // ps: 在设置userSessionMapping的Session_id时，需要通过设置关联对象来更新外键
    // mapping.setChatSession(newSession); 设置关联对象，而非直接修改sessionId字段

}

