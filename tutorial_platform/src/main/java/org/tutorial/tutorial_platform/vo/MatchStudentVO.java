package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Gender;
import org.tutorial.tutorial_platform.pojo.Student;

import java.math.BigDecimal;

@Data
public class MatchStudentVO {
    private Long userId;
    private String username;
    private Gender gender;
    private String grade;
    private String subject;
    private BigDecimal score;
    private String hobby;
    private String goal;
    public MatchStudentVO(Student student) {
        this.userId = student.getUser().getUserId();
        this.username = student.getName();
        this.gender = student.getGender();
        this.grade = student.getGrade();
        this.subject = student.getSubject();
        this.score = student.getScore();
        this.hobby = student.getHobby();
        this.goal = student.getGoal();

    }

}
