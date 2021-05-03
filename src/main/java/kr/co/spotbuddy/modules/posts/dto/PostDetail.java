package kr.co.spotbuddy.modules.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class PostDetail {

    private Long id;
    private String nickname;
    private boolean isAnonymous;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy-MM-dd HH:mm")
    private LocalDateTime postsTime;
    private int viewCount;

    private String title;
    private String content;

    private int commentsCount;

    private List<String> photoUrls;
}
