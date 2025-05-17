package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Teacher;

/**
 * 返回老师信息
 * @author zhj
 */
@Data
public class TeacherInfoVO {
    private String gender;
    private String education;
    private String teachGrade;
    private String subject;
    private String address;
    public TeacherInfoVO(TeacherInfoVO teacherInfoVO) {
        this.gender = teacherInfoVO.getGender();
        this.education = teacherInfoVO.getEducation();
        this.teachGrade = teacherInfoVO.getTeachGrade();
        this.subject = teacherInfoVO.getSubject();
        this.address = teacherInfoVO.getAddress();

    }

    public TeacherInfoVO() {

    }

    public TeacherInfoVO(Teacher teacher) {
        this.gender = teacher.getGender();
        this.education = teacher.getEducation();
        this.teachGrade = teacher.getTeachGrade();
        this.subject = teacher.getSubject();

    }
}
