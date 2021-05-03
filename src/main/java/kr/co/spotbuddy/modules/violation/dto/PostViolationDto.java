package kr.co.spotbuddy.modules.violation.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PostViolationDto {
    private int violationIndex;
    private String etc;
}
