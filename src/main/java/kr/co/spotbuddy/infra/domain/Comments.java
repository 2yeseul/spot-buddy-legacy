package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
public class Comments {

    @Id
    @GeneratedValue
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member member;

    private boolean isAnonymous;

    private LocalDateTime commentsTime;

    private String comment;

    private boolean isReply;

    @ColumnDefault("-1")
    private Long replyId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Posts posts;

    @JsonManagedReference
    @OneToMany(mappedBy = "comments", cascade = CascadeType.ALL)
    private Set<CommentsLike> commentsLikes = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "comments", cascade = CascadeType.ALL)
    private Set<Violation> violations = new LinkedHashSet<>();

    public void modifyComment(String comment) {
        this.comment = comment;
    }

    public void modifyAnonymousStatus(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    // soft delete
    private boolean deleteState;

    public void deleteComment() {
        this.deleteState = true;
    }
}
