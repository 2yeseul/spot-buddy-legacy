package kr.co.spotbuddy.modules.message.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TourInfo {
    private Long tourId;
    private String nickname;
    private String tourTitle;
}
