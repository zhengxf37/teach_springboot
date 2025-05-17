package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 教师实体类，对应数据库表 teacher
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

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "education", nullable = false, length = 10)
    private String education;

    @Column(name = "teach_grade", nullable = false, length = 10)
    private String teachGrade; // 可选值："小学" / "初中" / "高中"

    @Column(name = "subject", nullable = false, length = 30)
    private String subject;
    @Column(name = "address", nullable = false, length = 255)
    private String address;
}
