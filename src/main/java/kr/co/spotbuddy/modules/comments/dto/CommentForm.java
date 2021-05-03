package kr.co.spotbuddy.modules.comments.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CommentForm {

    private boolean isAnonymous;
    private String comment;
    private Long postId;
    private boolean replyStatus;
    private Long replyId;
}
