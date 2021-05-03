package kr.co.spotbuddy.modules.tour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class TourDetail {

    private Long id;
    
    private String nickname;
    private String tourLocation;

    private int teamIndex;

    private String tourTeam;

    private int nowNumberOfMember;

    private int requiredGender;
    private int minimumAge;
    private int maximumAge;

    private String tourTitle;
    private String tourContent;

    private int weather;

    private int scrapCount;

    private int viewCount;

    private int age;
    private int gender;

    private String tourDateDetail;
    private String bio;

    private List<String> tourThemes;

    private int minimumMember;
    private int maximumMember;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tourStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tourEndDate;

}
