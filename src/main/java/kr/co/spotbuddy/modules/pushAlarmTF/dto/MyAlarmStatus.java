package kr.co.spotbuddy.modules.pushAlarmTF.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MyAlarmStatus {
    private boolean messageTurnOn;

    private boolean activityTurnOn;

    private boolean scheduleTurnOn;

    private boolean promoTurnOn;
}
