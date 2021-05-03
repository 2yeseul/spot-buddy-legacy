package kr.co.spotbuddy.infra.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
public class PostsHide {

    @Id @GeneratedValue
    private Long id;

    private Long memberId;

    private Long postsId;
}
