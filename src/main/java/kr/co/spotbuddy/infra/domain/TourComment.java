package kr.co.spotbuddy.infra.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourComment {

    @Id
    @GeneratedValue
    private Long id;

    private String nickname;

    private LocalDateTime commentTime;

    private String commentContent;

}
