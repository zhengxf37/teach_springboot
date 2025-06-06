package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.tutorial.tutorial_platform.pojo.Gender;
/**
 * 教师信息更新DTO
 * 和数据库对应
 * @author ：zhj
 */
@Data
public class StudentInfoUpdateDTO {

    private Long userId;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名长度不能超过20个字符")
    private String name;

    @NotNull(message = "性别不能为空")
    private Gender gender;

    @NotBlank(message = "年级不能为空")
    private String grade;

    @NotBlank(message = "科目不能为空")
    private String subject;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotBlank(message = "手机号码不能为空")
    private String phone;

    @NotNull(message = "成绩不能为空")
    private Double score;

    @NotBlank(message = "兴趣不能为空")
    private String hobby;

    @NotBlank(message = "目标不能为空")
    private String goal;

    @NotBlank(message = "其他信息不能为空")
    private String addition;

}
