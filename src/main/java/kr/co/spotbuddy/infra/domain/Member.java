package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@DynamicUpdate
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Comments> comments = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Posts> posts = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "tourUploader", cascade = CascadeType.ALL)
    private Set<Chat> getChats = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "requestMember", cascade = CascadeType.ALL)
    private Set<Chat> sendChats = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "getMember", cascade = CascadeType.ALL)
    private Set<TourReview> getReviews = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "sendMember", cascade = CascadeType.ALL)
    private Set<TourReview> sendReviews = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<ConfirmedTour> confirmedTours = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Tour> tours = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Scrap> scraps = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "doBlock", cascade = CascadeType.ALL)
    private Set<Block> doBlocks = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "getBlocked", cascade = CascadeType.ALL)
    private Set<Block> getBlocked = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<ProfilePhoto> profilePhotos = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "doViolate", cascade = CascadeType.ALL)
    private Set<Violation> doViolations = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "doReport", cascade = CascadeType.ALL)
    private Set<Violation> doReports = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Alarm> alarms = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<TokenInfo> tokenInfos = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<PushAlarmTF> pushAlarmTFS = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<CommentsLike> commentsLikes = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<ScrapCommunity> scrapCommunities = new LinkedHashSet<>();

    @Column(unique = true)
    private String email;

    private boolean emailVerified;
    private String emailCheckToken;
    private LocalDateTime emailCheckTokenGeneratedAt;

    private String nickname;

    private String name;

    private String birth;

    private String password;

    private int teamIndex;

    private boolean isAgeOlderThan14;

    private boolean isAgreeOnTOS;

    private boolean isAgreeOnGetPromotion;

    private int gender;

    @ColumnDefault("50")
    private int weather;

    // soft delete
    private boolean deleteState;

    public void removeMember() {
        this.deleteState = true;
    }

    public void updateWeather(int score) {
        this.weather += score;
    }

    public void generatedEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public void resetPassword(String password) {
        this.password = password;
    }

    public void modifyMemberInfo(String nickname, String password, String birth, int gender, int teamIndex) {
        this.nickname = nickname;
        this.password = password;
        this.birth = birth;
        this.gender = gender;
        this.teamIndex = teamIndex;
    }

}
