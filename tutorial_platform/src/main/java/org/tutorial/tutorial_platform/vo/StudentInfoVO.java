package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Gender;
import org.tutorial.tutorial_platform.pojo.Student;

import java.math.BigDecimal;

@Data
public class StudentInfoVO {
    private Gender gender;
    private String grade;
    private String subject;
    private String address;
    private String phone;
    private BigDecimal score;
    private String hobby;
    private String goal;
    private String addition;

    public StudentInfoVO(Student student) {
        this.gender = student.getGender();
        this.grade = student.getGrade();
        this.subject = student.getSubject();
        this.address = student.getAddress();
        this.phone = student.getPhone();
        this.score = student.getScore();
        this.hobby = student.getHobby();
        this.goal = student.getGoal();
        this.addition = student.getAddition();
    }

    public StudentInfoVO() {}
}
