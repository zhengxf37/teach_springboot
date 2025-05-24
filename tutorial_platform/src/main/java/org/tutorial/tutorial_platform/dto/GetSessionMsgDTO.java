package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetSessionMsgDTO {
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    @Min(value = 0, message = "页码必须≥0")
    private int page = 0;  // 默认值在字段定义,根据传入参数动态调整

    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private int size = 10;
}
