package kr.co.spotbuddy.modules.tourReview.dto;

import kr.co.spotbuddy.infra.domain.SimpleTourReview;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class ReviewList {
    private String getReviewed;
    private String sendReview;
    private List<SimpleTourReview> simpleTourReviews;
    private String detailReview;
    private int weatherIndex;
    private boolean isAnonymous;
    private String reviewDate;
}
