package kr.co.spotbuddy.modules.posts.dto;

import lombok.Data;

@Data
public class PostSearch {
    private String keyword;
    private int teamIndex;
    private int category;
}
