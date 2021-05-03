package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
public class Posts {

    @Id @GeneratedValue
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member member;

    private int category;
    private int teamIndex;

    private String title;
    private String content;

    private boolean isAnonymous;

    private LocalDateTime postsTime;

    private int viewCount;

    private int todayView;

    @JsonManagedReference
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL)
    private Set<Comments> comments = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL)
    private Set<FileURL> fileURLS = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL)
    private Set<Violation> violations = new LinkedHashSet<>();

    // soft delete
    private boolean deleteState;

    public void update(String title, String content, int category, boolean isAnonymous) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.isAnonymous = isAnonymous;
    }

}
