package kr.co.spotbuddy.modules.tourReview.simpleTourReview;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.SimpleTourReview;
import kr.co.spotbuddy.infra.domain.TourReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimpleTourReviewRepository extends JpaRepository<SimpleTourReview, Long> {

    List<SimpleTourReview> findAllByTourReview(TourReview tourReview);

    List<SimpleTourReview> findAllByGetMemberOrderByReviewIndexDesc(Member getMember);

    List<SimpleTourReview> findAllByGetMember(Member getMember);

    int countByGetMemberAndReviewIndex(Member getMember, int reviewIndex);

    int countByGetMember(Member getMember);
}
