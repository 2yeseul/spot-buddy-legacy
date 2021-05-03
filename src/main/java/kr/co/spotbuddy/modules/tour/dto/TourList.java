package kr.co.spotbuddy.modules.tour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TourList {

    private Long id;

    private String tourLocation;
    private String nickname;
    private String tourTitle;

    private int weather;

    private int scrapCount;

    private int viewCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tourStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tourEndDate;

    private boolean isEnded;

    private String tourState;

}
