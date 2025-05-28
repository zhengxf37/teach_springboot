package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 用户评论的存储
 */
@Entity
@Table(name = "user_comment")
@Data
public class UserComment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    // 被评论者id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    // 评论者id
    @Column(name = "from_id", nullable = false)
    private Long fromId;
    @Column(name = "content", columnDefinition = "TEXT NOT NULL")
    private String content;
}
