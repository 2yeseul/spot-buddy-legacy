package kr.co.spotbuddy.modules.comments.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CommentModify {

    private Long commentId;
    private String comment;
    private boolean isAnonymous;
}
