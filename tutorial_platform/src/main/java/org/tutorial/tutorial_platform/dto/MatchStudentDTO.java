package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 教师发送的匹配请求参数
 */
@Data
public class MatchStudentDTO {
    @NotNull(message = "科目不能为空")
    private String subject;

    @NotNull(message = "年级不能为空")
    private String grade;

    private Double minScore = 80.0; // 默认最低成绩
}
