package kr.co.spotbuddy.modules.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder @Data
public class MyComments {
    // 댓글 내용, teamIndex, 작성일, 조회수, 댓글 수 , 글 제목
    private Long postId;
    private Long commentId;

    private String comment;
    private int teamIndex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime postsTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime commentsTime;

    private int viewCount;
    private int commentCount;

    private String title;

}
