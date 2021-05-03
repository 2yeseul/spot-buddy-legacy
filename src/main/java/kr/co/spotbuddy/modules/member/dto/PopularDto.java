package kr.co.spotbuddy.modules.member.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PopularDto {
    private int reviewIndex;
    private int ratio;
}
