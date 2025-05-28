package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JudgeUserDTO {
    //当前用户
    private Long userId;

    //评价对象
    @NotNull(message = "评价对象用户ID不能为空")
    private Long judgeId;
    @NotNull(message = "评价内容不能为空")
    private String content;
}
