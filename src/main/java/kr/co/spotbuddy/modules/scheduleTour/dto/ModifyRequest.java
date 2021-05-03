package kr.co.spotbuddy.modules.scheduleTour.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String memo;
}
