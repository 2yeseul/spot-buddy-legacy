package kr.co.spotbuddy.infra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.spotbuddy.modules.scheduleTour.dto.ModifyRequest;
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
public class ScheduleTour {

    @Id @GeneratedValue
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    private String memo;

    private Long tourId;

    private Long memberId;

    public void modifyScheduleContent(ModifyRequest modifyRequest) {
        this.startTime = modifyRequest.getStartTime();
        this.endTime = modifyRequest.getEndTime();
        this.memo = modifyRequest.getMemo();
    }

}
