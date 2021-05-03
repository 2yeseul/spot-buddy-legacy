package kr.co.spotbuddy.modules.tour.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourSave {

    private String tourLocation;
    private String tourTeam;

    private String tourTitle;
    private String tourContent;

    private int requiredGender;
    private int minimumAge;
    private int maximumAge;

    private int nowNumberOfMember;

    private int minimumMember;
    private int maximumMember;

    private String tourDateDetail;
    private String bio;

    private List<String> tourThemes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tourStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tourEndDate;

    private boolean isTempSaved;

}
