package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TourRepository extends JpaRepository<Tour, Long> {


    Tour findFirstByIsTempSavedAndMemberOrderByIdDesc(boolean isTempSaved, Member member);

    default Tour latestTempTour(Member member) {
        return findFirstByIsTempSavedAndMemberOrderByIdDesc(true, member);
    }

    boolean existsByIsTempSavedAndMember(boolean isTempSaved, Member member);

    List<Tour> findAllByIsTempSavedAndMember(boolean isTempSaved, Member member);

    void deleteAllByIsTempSavedAndMember(boolean isTempSaved, Member member);

    Tour findByIsTempSavedAndMember(boolean isTempSaved, Member member);

    List<Tour> findAllByIdNotInAndIsEndedNotAndIsTempSavedNotAndDeleteStateNot(List<Long> ids, boolean isEnded, boolean isTempSaved, boolean deleteState);

    Page<Tour> findAllByIdNotInAndIsEndedNotAndIsTempSavedNotAndDeleteStateNot(List<Long> ids, boolean isEnded, boolean isTempSaved, boolean deleteState, Pageable pageable);

    Page<Tour> findAll(Pageable pageable);

    Page<Tour> findAllByIsEndedNotAndIsTempSavedNotAndDeleteStateNot(boolean isEnded, boolean isTempSaved, boolean deleteState, Pageable pageable);


    List<Tour> findAllByMemberAndDeleteStateNot(Member member, boolean deleteState);

    List<Tour> findAllByMemberAndDeleteStateNotOrderByIdDesc(Member member, boolean deleteState);

    List<Tour> findAllByIsEndedNotAndIsTempSavedNotAndDeleteStateNot(boolean isEnded, boolean isTempSaved, boolean deleteState);


    default List<Tour> myTourUploadList(Member member) {
        return findAllByMemberAndDeleteStateNotOrderByIdDesc(member, true);
    }

    default List<Tour> getFilteredPopularList(List<Long> ids) {
        return findAllByIdNotInAndIsEndedNotAndIsTempSavedNotAndDeleteStateNot(ids, true, true, true);
    }

    default Page<Tour> getFilteredLatestPages(List<Long> ids, Pageable pageable) {
        return findAllByIdNotInAndIsEndedNotAndIsTempSavedNotAndDeleteStateNot(ids,true, true, true, pageable);
    }
    // 전체 리스트 조회 - 제외 : 모집 마감, 임시저장, 삭제
    default Page<Tour> getAllTourPages(Pageable pageable) {
        return findAllByIsEndedNotAndIsTempSavedNotAndDeleteStateNot(true, true, true, pageable);
    }

    default List<Tour> getAllTourList() {
        return findAllByIsEndedNotAndIsTempSavedNotAndDeleteStateNot(true, true, true);
    }


}
