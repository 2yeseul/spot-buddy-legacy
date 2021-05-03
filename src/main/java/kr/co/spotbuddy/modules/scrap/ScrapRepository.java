package kr.co.spotbuddy.modules.scrap;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Scrap;
import kr.co.spotbuddy.infra.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    boolean existsByMember(Member member);

    boolean existsByTour_Id(Long Id);

    boolean existsByMemberAndTour(Member member, Tour tour);

    Scrap findByMember(Member member);

    List<Scrap> findByTour(Tour tour);

    Scrap findByMemberAndTour(Member member, Tour tour);

    List<Scrap> findAllByMemberOrderByIdDesc(Member member);

    int countByTour(Tour tour);
}
