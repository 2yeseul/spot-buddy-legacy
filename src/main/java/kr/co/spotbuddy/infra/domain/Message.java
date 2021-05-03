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
@AllArgsConstructor @NoArgsConstructor
@DynamicUpdate
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    private String messageTime;

    private boolean readStatus;

    // flush
    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Member sender;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Chat chat;

    // soft delete
    private boolean deleteState;

    public void removeMember() {
        this.deleteState = true;
    }

    public void readStatusTrue(boolean readStatus) {
        this.readStatus = readStatus;
    }

}
