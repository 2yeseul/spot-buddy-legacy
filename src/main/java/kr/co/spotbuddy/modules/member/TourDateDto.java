package kr.co.spotbuddy.modules.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data @Builder
public class TourDateDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tourStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tourEndDate;
}
