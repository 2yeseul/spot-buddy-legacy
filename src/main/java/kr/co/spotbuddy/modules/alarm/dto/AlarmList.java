package kr.co.spotbuddy.modules.alarm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class AlarmList {
    private Long alarmId;
    private int alarmType;
    private Long alarmedObjectId;
    private boolean readStatus;
    private String title;
    private String body;
    private int teamIndex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy-MM-dd EEE HH:mm", locale = "ko")
    private LocalDateTime alarmDate;
}
