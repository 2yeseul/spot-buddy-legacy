package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.infra.domain.TourTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TourSearchRepository extends JpaRepository<Tour, Long> {


    List<Tour> findAllByStartDateGreaterThanEqualAndEndDateLessThanEqualOrderByIdDesc(LocalDate startDate, LocalDate endDate);

    List<Tour> findAllByTourLocationContainingOrTourTeamContainingOrderByIdDesc(String tourLocation, String tourTeam);

    List<Tour> findAllByTourLocationContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualOrderByIdDesc(String tourLocation, LocalDate startDate, LocalDate endDate);

    List<Tour> findAllByTourLocationAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(String tourLocation, LocalDate startDate, LocalDate endDate);

    List<Tour> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(LocalDate startDate, LocalDate endDate);

    List<Tour> findAllByTourTitleContainingOrTourContentContainingOrderByIdDesc(String tourTitle, String tourContent);

    boolean existsByTourTitleContainingOrTourContentContaining(String tourTitle, String tourContent);

    default List<Tour> searchByKeywordAtHome(String keyword) {
        return findAllByTourTitleContainingOrTourContentContainingOrderByIdDesc(keyword, keyword);
    }

    default List<Tour> searchByKeyword(String keyword) {
        return findAllByTourLocationContainingOrTourTeamContainingOrderByIdDesc(keyword, keyword);
    }

    default List<Tour> searchByLocationAndDate(String tourLocation, LocalDate startDate, LocalDate endDate) {
        if(startDate.equals(endDate)) {
            return findAllByTourLocationAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(tourLocation, startDate, endDate);
        }
        return findAllByTourLocationContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualOrderByIdDesc(tourLocation, startDate, endDate);
    }

    default List<Tour> searchByDate(LocalDate startDate, LocalDate endDate) {
        if(startDate.equals(endDate)) {
            return findAllByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(startDate, endDate);
        }
        return findAllByStartDateGreaterThanEqualAndEndDateLessThanEqualOrderByIdDesc(startDate, endDate);
    }

}
