package kr.co.spotbuddy.modules.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class CommentsList {

    private Long commentId;
    private String nickname;
    private int likeCount;
    private boolean isAnonymous;
    private String comment;
    private boolean replyStatus;
    private Long replyId;
    private int teamIndex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime commentTime;
}
