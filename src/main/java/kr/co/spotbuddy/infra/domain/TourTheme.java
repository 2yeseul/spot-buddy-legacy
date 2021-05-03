package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourTheme {

    @Id @GeneratedValue
    private Long id;

    private String theme;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Tour tour;

    public void update(String theme) {
        this.theme = theme;
    }
}
