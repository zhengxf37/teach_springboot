package org.tutorial.tutorial_platform.vo;

import lombok.Data;

@Data
public class UserCommentVO {
    private Long fromId;
    private String content;

    public UserCommentVO(Long fromId, String content) {
        this.fromId = fromId;
        this.content = content;
    }
}
