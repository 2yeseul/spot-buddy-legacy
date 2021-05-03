package kr.co.spotbuddy.infra.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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
@DynamicUpdate
public class PasswordFind {

    @Id @GeneratedValue
    private Long id;

    private String email;

    private int token;

    private boolean isConfirmed;

    private LocalDateTime sendEmailTime;

    private LocalDateTime confirmEmailTime;

    public void mailConfirmed() {
        this.isConfirmed = true;
    }

    public void updateConfirmTime() {
        this.confirmEmailTime = LocalDateTime.now();
    }

    public void updateSendTime() {
        this.sendEmailTime = LocalDateTime.now();
    }
}
