package kr.co.spotbuddy.modules.violation.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MemberViolationDto {
    private int violationIndex;
    private String etc;
    private String nickname;
}
