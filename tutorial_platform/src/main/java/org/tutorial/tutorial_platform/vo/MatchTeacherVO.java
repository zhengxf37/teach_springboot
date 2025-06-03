package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Education;
import org.tutorial.tutorial_platform.pojo.Gender;
import org.tutorial.tutorial_platform.pojo.TeachGrade;
import org.tutorial.tutorial_platform.pojo.Teacher;

import java.math.BigDecimal;

@Data
public class MatchTeacherVO {
    private Long userId;
    private String username;
    private Gender gender;
    private Education education;
    private TeachGrade teachGrade;
    private String subject;
    private BigDecimal score;
    private Integer experience;
    private String hobby;


    public MatchTeacherVO(Teacher teacher) {
        this.userId = teacher.getUser().getUserId();
        this.username = teacher.getName();
        this.gender = teacher.getGender();
        this.education = teacher.getEducation();
        this.teachGrade = teacher.getTeachGrade();
        this.subject = teacher.getSubject();
        this.experience = teacher.getExperience();
        this.score = teacher.getScore();
        this.hobby = teacher.getHobby();
    }
    public MatchTeacherVO() {
    }
}
