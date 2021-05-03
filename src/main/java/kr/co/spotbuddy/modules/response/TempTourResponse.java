package kr.co.spotbuddy.modules.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TempTourResponse {
    private boolean isExists;
    private Long tourId;
}
