package kr.co.spotbuddy.modules.member.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LatestDto {
    private String nickname;
    private String review;
    private boolean anonymous;
    private String reviewDate;
    private int weatherIndex;
}
