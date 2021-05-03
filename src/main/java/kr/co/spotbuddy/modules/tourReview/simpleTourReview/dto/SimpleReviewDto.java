package kr.co.spotbuddy.modules.tourReview.simpleTourReview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleReviewDto {
    private String nickname;
    private int reviews;
}
