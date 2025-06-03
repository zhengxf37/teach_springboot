package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;

/**
 * Teacher -老师实体类
 * 和数据库对应
 * 元信息：
 * @author zhj
 * @version 1.0
 * @since 2025-05-17
 */
@Entity
@Table(name = "teacher")
@Data
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "education", nullable = false)
    private Education education;

    @Enumerated(EnumType.STRING)
    @Column(name = "teach_grade", nullable = false)
    private TeachGrade teachGrade;

    @Column(name = "subject", nullable = false, length = 30)
    private String subject;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    @Column(name = "experience", nullable = false)
    private Integer experience;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "hobby", nullable = false, length = 255)
    private String hobby;

    @Column(name = "school", nullable = false, length = 100)
    private String school;

    @Column(name = "addition", columnDefinition = "TEXT NOT NULL")
    private String addition;
    @Column(name = "vector", columnDefinition = "JSON")
    @Convert(converter = DoubleListJsonConverter.class)
    private List<Double> vector; // 直接存储为 JSON 数组
}
