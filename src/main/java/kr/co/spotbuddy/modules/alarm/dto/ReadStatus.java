package kr.co.spotbuddy.modules.alarm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ReadStatus {
    private boolean alarmRead;
}
