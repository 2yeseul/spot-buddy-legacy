package kr.co.spotbuddy.modules.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProfileView {

    private String nickname;
    private String birth;
    private int weather;
    private int confirmedTourCount;
    private int teamIndex;
    private int gender;

    private List<PopularDto> popularReviews;
    private List<LatestDto> latestReviews;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime joinDate;
}
