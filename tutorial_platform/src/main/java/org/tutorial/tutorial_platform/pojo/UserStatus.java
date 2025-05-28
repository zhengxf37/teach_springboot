package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_status")
@Data
public class UserStatus {
    @Id
    @Column(name = "user_id")
    private Long userId; // 主键字段类型与 User 的主键一致

    @OneToOne
    @MapsId // 将 UserStatus 的主键映射到 User 的主键
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "status", columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer status;

    @Column(name = "want_id", columnDefinition = "BIGINT NOT NULL DEFAULT 0")
    private Long wantId;
}