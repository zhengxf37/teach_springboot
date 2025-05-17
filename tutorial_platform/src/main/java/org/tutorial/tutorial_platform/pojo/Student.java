package org.tutorial.tutorial_platform.pojo;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 学生实体类，对应数据库表 student
 * @author ：zhj
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

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "grade", nullable = false, length = 20)
    private String grade;

    @Column(name = "subject", nullable = false, length = 30)
    private String subject;
    @Column(name = "address", nullable = false, length = 255)
    private String address;
}
