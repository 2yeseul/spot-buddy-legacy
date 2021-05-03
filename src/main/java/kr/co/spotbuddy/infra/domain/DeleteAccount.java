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
public class DeleteAccount {

    @Id @GeneratedValue
    private Long id;

    private String email;

    private int reasonIndex;

    private String etc;

    private LocalDateTime deleteTime;

}
