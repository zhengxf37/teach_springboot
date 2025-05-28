package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AiRequest - AI 请求数据封装类
 *
 * 用于接收客户端发送到 AI 接口的请求体，包含用户输入的提示词。
 *
 * 元信息：
 * @author zhj
 * @version 1.0
 * @since 2025-05
 */

@Data
public class AiRequestDTO {

    /**
     * 用户输入的提示词（问题或指令）
     * 非空且长度限制在 1~1000 字符之间
     * {
     *   "prompt": "请解释什么是机器学习？"
     * }
     */
    @NotBlank(message = "提示词不能为空")
    @Size(max = 1000, message = "提示词长度不能超过1000个字符")
    private String prompt;

}
