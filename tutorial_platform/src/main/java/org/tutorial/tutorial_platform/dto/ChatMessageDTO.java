package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatMessageDTO {
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    @NotNull(message = "发送者ID不能为空")
    private Long senderId;

    @NotNull(message = "接收方ID不能为空")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000,message = "消息内容不能超过1000个字符")
    private String content;

} 