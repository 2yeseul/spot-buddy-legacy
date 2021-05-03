package kr.co.spotbuddy.modules.posts.dto;

import lombok.Data;

@Data
public class BadWordsRequest {
    private String title;
    private String content;
}
