package kr.co.spotbuddy.modules.confirmedTour;

import kr.co.spotbuddy.infra.domain.ConfirmedTour;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmedTourRepository extends JpaRepository<ConfirmedTour, Long> {

    List<ConfirmedTour> findAllByMemberOrderByIdDesc(Member member);


    List<ConfirmedTour> findAllByTour(Tour tour);

    boolean existsByTour(Tour tour);

    boolean existsByChatIdAndMember(Long chatId, Member member);

    boolean existsByTourAndMember(Tour tour, Member member);

    ConfirmedTour findByTourAndMember(Tour tour, Member member);

    default List<ConfirmedTour> getMyConfirmedTours(Member member) {
        return findAllByMemberOrderByIdDesc(member);
    }

    List<ConfirmedTour> findAllByMemberAndDeleteStateOrderById(Member member, boolean deleteState);

    boolean existsByMemberAndTourAndDeleteState(Member member, Tour tour, boolean deleteState);

    default boolean isMemberCanceled(Member member, Tour tour) {
        return existsByMemberAndTourAndDeleteState(member, tour, true);
    }

    boolean existsByMemberAndDeleteState(Member member, boolean deleteState);

    default boolean isAllConfirmed(Member tourUploader, Member requestMember, Long chatId) {
        return existsByChatIdAndMember(chatId, tourUploader) && existsByChatIdAndMember(chatId, requestMember);
    }
}
