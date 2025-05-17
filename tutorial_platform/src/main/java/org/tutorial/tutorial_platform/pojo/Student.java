package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
/**
 * 学生实体类
 * 和数据库对应
 *
 * 元信息：
 * @author zhj
 * @version 1.0
 * @since 2025-05-17
 */
@Entity
@Table(name = "student")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "grade", nullable = false, length = 20)
    private String grade;

    @Column(name = "subject", nullable = false, length = 30)
    private String subject;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "hobby", nullable = false, length = 255)
    private String hobby;

    @Column(name = "goal", nullable = false, length = 255)
    private String goal;

    @Column(name = "addition", columnDefinition = "TEXT NOT NULL")
    private String addition;
}
