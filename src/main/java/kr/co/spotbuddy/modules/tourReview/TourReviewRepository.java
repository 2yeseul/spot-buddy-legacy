package kr.co.spotbuddy.modules.tourReview;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.TourReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourReviewRepository extends JpaRepository<TourReview, Long> {

    List<TourReview> findAllByGetMember(Member getMember);

    List<TourReview> findAllByGetMemberOrderBySimpleTourReviews(Member getMember);

    List<TourReview> findAllByGetMemberOrderByIdDesc(Member getMember);

    List<TourReview> findAllBySendMember(Member sendMember);

    boolean existsByGetMember(Member member);
    int countByGetMember(Member getMember);

    boolean existsByGetMemberAndSendMember(Member getMember, Member sendMember);

    TourReview findTopByGetMemberAndSendMemberOrderByIdDesc(Member getMember, Member sendMember);

    default TourReview latestReview(Member getMember, Member sendMember) {
        return findTopByGetMemberAndSendMemberOrderByIdDesc(getMember, sendMember);
    }
}
