package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Alarm {

    @Id @GeneratedValue
    private Long id;

    private int alarmType; // 1. 전체 공지 2. 동행 확정 3. 커뮤니티 댓글 4. 커뮤니티 댓글 좋아요

    private Long alarmedObject;

    private boolean readStatus;

    private String title;

    private String body;

    private LocalDateTime alarmDate;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member member;

    public void readTrue() {
        this.readStatus = true;
    }
}
