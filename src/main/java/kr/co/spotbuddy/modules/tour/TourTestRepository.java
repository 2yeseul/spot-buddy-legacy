package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.infra.domain.TourTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TourTestRepository extends JpaRepository<TourTest, Long> {

    List<TourTest> findAllByStartDateLessThanEqualOrEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
}
