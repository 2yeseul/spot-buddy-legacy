package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Chat {

    @Id
    @GeneratedValue
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member tourUploader;

    private boolean isTourUploaderRead;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member requestMember;

    private boolean isRequestMemberRead;

    private boolean tourUploaderEnter;
    private boolean requestMemberEnter;

    @JsonManagedReference
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Tour tour;

    private boolean tourUploaderDelete;
    private boolean requestMemberDelete;

    public void deleteChatRoom(String fromWho) {
        if(fromWho.equals("tourUploader")) {
            this.tourUploaderDelete = true;
        }
        else {
            this.requestMemberDelete = true;
        }
    }

    public void resetDeleteState () {
        this.tourUploaderDelete = false;
        this.requestMemberDelete = false;
    }
    public void tourUploaderState(boolean isTourUploaderRead) {
        this.isTourUploaderRead = isTourUploaderRead;
    }

    public void requestMemberState(boolean isRequestMemberRead) {
        this.isRequestMemberRead = isRequestMemberRead;
    }

    public void setTourUploaderEnter() {
        this.tourUploaderEnter = true;
    }

    public void setTourUploaderOut() {
        this.tourUploaderEnter = false;
    }

    public void setRequestMemberEnter() {
        this.requestMemberEnter = true;
    }

    public void setRequestMemberOut() {
        this.requestMemberEnter = false;
    }

    public void tourUploaderEnterReset() {
        this.tourUploaderEnter = !tourUploaderEnter;
    }

    public void requestMemberEnterReset() {
        this.requestMemberEnter = !requestMemberEnter;
    }
}
