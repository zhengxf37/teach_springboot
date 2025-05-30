package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * 教师发送的匹配请求参数
 */
@Data
public class MatchStudentDTO {
    @NotNull(message = "studentId 不能为空")
    private Long studentId;

    @NotNull(message = "科目不能为空")
    private String subject;

    @NotNull(message = "年级不能为空")
    private String grade;

    private Double minScore = 80.0; // 默认最低成绩

    private int page = 0;      // 第几页，从0开始
    private int size = 10;     // 每页数量

}
