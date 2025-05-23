package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Education;
import org.tutorial.tutorial_platform.pojo.Gender;
import org.tutorial.tutorial_platform.pojo.TeachGrade;
import org.tutorial.tutorial_platform.pojo.Teacher;

import java.math.BigDecimal;
/**
 * 老师特色信息返回类
 * 和数据库对应
 *
 * 元信息：
 * @author zhj
 * @version 1.0
 * @since 2025-05-17
 */
@Data
public class TeacherInfoVO {
    private String name;
    private Gender gender;
    private Education education;
    private TeachGrade teachGrade;
    private String subject;
    private String address;
    private String phone;
    private Integer experience;
    private BigDecimal score;
    private String hobby;
    private String school;
    private String addition;


    public TeacherInfoVO(Teacher teacher) {
        this.name = teacher.getName();
        this.gender = teacher.getGender();
        this.education = teacher.getEducation();
        this.teachGrade = teacher.getTeachGrade();
        this.subject = teacher.getSubject();
        this.address = teacher.getAddress();
        this.phone = teacher.getPhone();
        this.experience = teacher.getExperience();
        this.score = teacher.getScore();
        this.hobby = teacher.getHobby();
        this.school = teacher.getSchool();
        this.addition = teacher.getAddition();
    }

    public TeacherInfoVO() {}
}
