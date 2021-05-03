package kr.co.spotbuddy.modules.confirmedTour.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CancelResponse {

    private boolean canceled;
}
