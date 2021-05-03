package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class TokenInfo {

    @Id @GeneratedValue
    private Long id;

    private String token;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member member;

    public boolean isEqualsWithToken(String token) {
        return this.token.equals(token);
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
