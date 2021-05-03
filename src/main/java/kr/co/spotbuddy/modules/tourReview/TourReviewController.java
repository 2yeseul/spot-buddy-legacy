package kr.co.spotbuddy.modules.tourReview;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.tourReview.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TourReviewController {

    private final MemberType memberType;
    private final TourReviewService tourReviewService;
    private final MemberRepository memberRepository;

    // 동행 평가
    @PostMapping("/tour-review")
    public void doTourReview(@CurrentUser Object object, @RequestBody TourReviewDto tourReviewDto) {
        Member member = memberType.getMemberType(object);
        tourReviewService.uploadTourReviewProcess(tourReviewDto, member);
    }

    @GetMapping("/my-tour-review")
    public List<ReviewList> getMyTourReview(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
       return tourReviewService.getMyTourReviews(member);
   }

   // 동행 평가 상세 조회
   @PostMapping("/tour-review/detail")
   public List<ReviewList> getTourReviewDetails(@RequestBody TourReviewDetail tourReviewDetail) {
        Member member = memberRepository.findByNickname(tourReviewDetail.getNickname());
        return tourReviewService.getMyTourReviews(member);
   }

   // 평가 가능한 지 여부
   @PostMapping("/review-possible")
   public ReviewPossible isReviewPossible(@CurrentUser Object object, @RequestBody YourNickname yourNickname) {
        Member member = memberType.getMemberType(object);
        return tourReviewService.isMemberReviewToday(member, yourNickname);
   }
}
