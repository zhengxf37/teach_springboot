package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * 学生发送的匹配请求参数
 */
@Data
public class MatchTeacherDTO {
    @NotNull(message = "teacherId 不能为空")
    private Long teacherId;

    @NotNull(message = "科目不能为空")
    private String subject;

    @NotNull(message = "年级不能为空")
    private String grade;

    private Double minScore = 4.5; // 默认最低评分

    private int page = 0;      // 第几页，从0开始
    private int size = 10;     // 每页数量
}
