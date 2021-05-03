package kr.co.spotbuddy.infra.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class AppVersion {

    @Id @GeneratedValue
    private Long id;

    private String token;

    private String version;

    public void updateVersion(String version) {
        this.version = version;
    }
}
