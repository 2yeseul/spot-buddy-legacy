package kr.co.spotbuddy.modules.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ConfirmTourResponse {
    private boolean isConfirmed;
}
