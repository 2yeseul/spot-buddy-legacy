package kr.co.spotbuddy.modules.tourTheme;

import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.infra.domain.TourTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourThemeRepository extends JpaRepository<TourTheme, Long> {

    List<TourTheme> findAllByTour(Tour tour);

    void deleteAllByTour(Tour tour);

    boolean existsByTour(Tour tour);

    List<TourTheme> findAllByTheme(String theme);
}
