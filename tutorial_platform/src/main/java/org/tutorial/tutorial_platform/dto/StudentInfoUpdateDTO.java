package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * StudentUpdateDTO - 学生信息更新数据传输对象
 *
 * 封装学生用户更新时提交的数据，包括：
 * - 性别
 * - 年级
 * - 科目
 * 元信息：
 * @author zhj
 * @version 1.0
 * @since 2025-05-17
 */
@Data
public class StudentInfoUpdateDTO {

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
     * 学生所在年级
     */
    @NotBlank(message = "年级不能为空")
    private String grade;

    /**
     * 学生所学科目
     */
    @NotBlank(message = "科目不能为空")
    private String subject;
    /**
     * 学生地址
     */
    @NotBlank(message = "地址不能为空")
    private String address;
}
