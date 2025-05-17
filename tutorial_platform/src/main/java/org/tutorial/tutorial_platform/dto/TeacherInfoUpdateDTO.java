package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TeacherUpdateDTO - 教师信息更新数据传输对象
 *
 * 封装教师用户更新时提交的数据，包括：
 * - 性别
 * - 学历
 * - 教学年级
 * - 科目
 * @author zhj
 */
@Data
public class TeacherInfoUpdateDTO {
    /**
     * 用户ID
     */

    private Long userId;

    /**
     * 性别
     */
    @NotBlank(message = "性别不能为空")
    private String gender;

    /**
     * 学历
     */
    @NotBlank(message = "学历不能为空")
    private String education;

    /**
     * 教学年级（小学/初中/高中）
     */
    @NotBlank(message = "教学年级不能为空")
    private String teachGrade;

    /**
     * 科目
     */
    @NotBlank(message = "科目不能为空")
    private String subject;
    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    private String address;
}
