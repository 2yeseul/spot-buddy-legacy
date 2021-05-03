package kr.co.spotbuddy.infra.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    @Id @GeneratedValue
    private Long id;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

}
