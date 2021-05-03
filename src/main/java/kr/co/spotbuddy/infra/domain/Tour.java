package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Tour {

    @Id
    @GeneratedValue
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member member;

    private String tourLocation;
    private String tourTeam;

    private String tourStartDate;
    private String tourEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String tourTitle;
    private String tourContent;

    // 투어 조건
    private int requiredGender; // 1이면 남성 2이면 여성 3이면 제한 없음
    private int minimumAge;
    private int maximumAge;

    // 작성 시간
    private LocalDateTime postsAt;

    private int totalNumberOfMember;
    private int nowNumberOfMember;
    private int minimumMember;
    private int maximumMember;

    private int viewCount;

    private boolean isEnded;

    private String tourDateDetail;
    private String bio;

    private boolean isTempSaved;

    @JsonManagedReference
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<TourTheme> tourThemes = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<Chat> chats = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<Scrap> scraps = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "violatedTour", cascade = CascadeType.ALL)
    private Set<Violation> violations = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<ConfirmedTour> confirmedTours = new LinkedHashSet<>();

    // soft delete
    private boolean deleteState;

    public void removeTourArticle() {
        this.deleteState = true;
    }

    public void update(String tourLocation, String tourTeam, LocalDate tourStartDate, LocalDate tourEndDate, Member member, String tourTitle,
                       String tourContent, int requiredGender, int minimumAge, int maximumAge,
                       String tourDateDetail, String bio, int minimumMember, int maximumMember, boolean isTempSaved) {
        this.tourLocation = tourLocation;
        this.tourTeam = tourTeam;
        this.startDate = tourStartDate;
        this.endDate = tourEndDate;
        this.member = member;
        this.tourTitle = tourTitle;
        this.tourContent = tourContent;
        this.requiredGender = requiredGender;
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
        this.tourDateDetail = tourDateDetail;
        this.bio = bio;
        this.minimumMember = minimumMember;
        this.maximumMember = maximumMember;
        this.isTempSaved = isTempSaved;
    }

    public void updateNowMemberCount() {
        this.nowNumberOfMember++;
    }

    public void discountNowMemberCount() {
        this.nowNumberOfMember--;
    }

    public void closeTour() {
        this.isEnded = true;
    }

    public void updateTourDate(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
