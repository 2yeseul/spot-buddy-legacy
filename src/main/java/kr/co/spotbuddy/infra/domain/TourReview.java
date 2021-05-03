package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TourReview {

    @Id @GeneratedValue
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member getMember;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member sendMember;

    @JsonManagedReference
    @OneToMany(mappedBy = "tourReview", cascade = CascadeType.ALL)
    private Set<SimpleTourReview> simpleTourReviews = new LinkedHashSet<>();

    private String detailReview;

    private int weatherIndex;

    private boolean isAnonymous;

    // soft delete
    private boolean deleteState;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime reviewTime;

    public void removeMember() {
        this.deleteState = true;
    }

}
