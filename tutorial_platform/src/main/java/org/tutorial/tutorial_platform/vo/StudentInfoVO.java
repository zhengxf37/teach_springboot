package org.tutorial.tutorial_platform.vo;

import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Student;

/**
 * StudentInfoVO - 学生信息展示对象
 *
 * 用于封装返回给前端的学生用户详细信息，包括：
 * - 性别
 * - 所属年级
 * - 所学科目
 *
 * 使用场景：
 * - 查询学生信息接口的返回体
 *
 * 示例响应：
 * {
 *   "gender": "男",
 *   "grade": "高一",
 *   "subject": "数学"
 * }
 *
 * 元信息：
 * @author zhj
 * @version 1.0
 * @since 2025-05-17
 */
@Data
public class StudentInfoVO {
    /**
     * 学生性别
     */
    private String gender;

    /**
     * 学生所在年级
     */
    private String grade;

    /**
     * 学生所学科目
     */
    private String subject;
    /**
     * 地址
     */
    private String address;

    /**
     * 构造函数
     *
     * @param student 学生信息
     */
    public StudentInfoVO(Student student) {
        this.gender = student.getGender();
        this.grade = student.getGrade();
        this.subject = student.getSubject();
        this.address = student.getAddress();
    }
    public StudentInfoVO() {
    }
}
