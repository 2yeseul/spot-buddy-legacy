package kr.co.spotbuddy.modules.member.dto;

import lombok.Data;

@Data
public class AgreeOnTOS {
    private boolean isAgeOlderThan14;

    private boolean isAgreeOnTOS;

    private boolean isAgreeOnGetPromotion;
}
