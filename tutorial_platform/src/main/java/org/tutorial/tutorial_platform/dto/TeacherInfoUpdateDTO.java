package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Education;
import org.tutorial.tutorial_platform.pojo.Gender;
import org.tutorial.tutorial_platform.pojo.TeachGrade;

/**
 * 教师信息更新DTO
 * 和数据库对应
 * @author ：zhj
 */
@Data
public class TeacherInfoUpdateDTO {

    private Long userId;

    @NotNull(message = "性别不能为空")
    private Gender gender;

    @NotNull(message = "学历不能为空")
    private Education education;

    @NotNull(message = "教学年级不能为空")
    private TeachGrade teachGrade;

    @NotBlank(message = "科目不能为空")
    private String subject;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotBlank(message = "手机号码不能为空")
    private String phone;

    @NotNull(message = "教学经验不能为空")
    private Integer experience;

    @NotNull(message = "评分不能为空")
    private Double score;

    @NotBlank(message = "兴趣不能为空")
    private String hobby;

    @NotBlank(message = "毕业学校不能为空")
    private String school;

    @NotBlank(message = "其他信息不能为空")
    private String addition;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名长度不能超过20个字符")
    private String name;
}
