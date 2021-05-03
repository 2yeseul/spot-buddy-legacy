package kr.co.spotbuddy.modules.scheduleTour;

import kr.co.spotbuddy.infra.domain.ScheduleTour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTourRepository extends JpaRepository<ScheduleTour, Long> {
    boolean existsByMemberIdAndTourId(Long memberId, Long tourId);

    ScheduleTour findByMemberIdAndTourId(Long memberId, Long tourId);
}
