package kr.co.spotbuddy.modules.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class PostList {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime postsDate;
    private int viewCount;
    private int commentCount;

    private String title;
    private String content;

    private int teamIndex;

    private String photoUrl;

    private int category;
}
