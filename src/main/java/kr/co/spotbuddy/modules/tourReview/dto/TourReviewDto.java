package kr.co.spotbuddy.modules.tourReview.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class TourReviewDto {

    private String nickname;
    private Set<Integer> reviews;
    private String detailReview;
    private boolean isAnonymous;
    private int weatherIndex;

}
