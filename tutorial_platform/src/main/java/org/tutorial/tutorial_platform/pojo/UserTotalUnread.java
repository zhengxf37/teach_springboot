package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;

/**
 * UserUnreadSummary - 用户未读消息汇总实体类
 * 与数据库表 user_unread_summary 对应
 *
 * 元信息：
 * @author zxf
 */
@Data
@Entity
@Table(name = "user_total_unread")
public class UserTotalUnread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_unread", nullable = false)
    private Integer totalUnread = 0;
}