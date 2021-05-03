package kr.co.spotbuddy.modules.tourReview;

import com.google.common.collect.Lists;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.SimpleTourReview;
import kr.co.spotbuddy.infra.domain.TourReview;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.tourReview.dto.ReviewList;
import kr.co.spotbuddy.modules.tourReview.dto.ReviewPossible;
import kr.co.spotbuddy.modules.tourReview.dto.TourReviewDto;
import kr.co.spotbuddy.modules.tourReview.dto.YourNickname;
import kr.co.spotbuddy.modules.tourReview.simpleTourReview.SimpleTourReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TourReviewService {

    private final SimpleTourReviewRepository simpleTourReviewRepository;
    private final MemberRepository memberRepository;
    private final TourReviewRepository tourReviewRepository;

    // 사용자가 상대방에게 오늘 동행 평가를 남겼는지를 반환
    public ReviewPossible isMemberReviewToday(Member sender, YourNickname yourNickname) {
        LocalDateTime todayDateTime = LocalDateTime.now();
        LocalDate todayDate = todayDateTime.toLocalDate();

        Member getMember = memberRepository.findByNickname(yourNickname.getNickname());

        boolean isTodayReview = false;

        // 동행 평가를 한 번이라도 한 이력이 있을 때
        if(tourReviewRepository.existsByGetMemberAndSendMember(getMember, sender)) {
            TourReview latestReview = tourReviewRepository.latestReview(getMember, sender);
            LocalDate reviewDate = latestReview.getReviewTime().toLocalDate();

            // 마지막 동행평가의 날짜가 오늘일 때
            if(todayDate.equals(reviewDate)) isTodayReview = true;
        }

        return ReviewPossible.builder().isTodayReview(isTodayReview).build();
    }


    // 내가 받은 동행 평가
    public List<ReviewList> getMyTourReviews(Member member) {
        List<TourReview> tourReviews = tourReviewRepository.findAllByGetMemberOrderByIdDesc(member);
        List<ReviewList> reviewLists = new ArrayList<>();

        for(TourReview tourReview : tourReviews) {
            String reviewDate = reviewDateProcess(tourReview.getReviewTime());
            List<SimpleTourReview> simpleTourReviewList = Lists.newArrayList(tourReview.getSimpleTourReviews());

            simpleTourReviewList.sort(Comparator.comparing(SimpleTourReview::getReviewIndex));

            ReviewList reviewList = ReviewList.builder()
                    .getReviewed(member.getNickname())
                    .sendReview(tourReview.getSendMember().getNickname())
                    .simpleTourReviews(simpleTourReviewList)
                    .detailReview(tourReview.getDetailReview())
                    .weatherIndex(tourReview.getWeatherIndex())
                    .isAnonymous(tourReview.isAnonymous())
                    .reviewDate(reviewDate)
                    .build();

            reviewLists.add(reviewList);
        }
        return reviewLists;
    }

    public String reviewDateProcess(LocalDateTime reviewDate) {
        if(reviewDate == null) return "null";

        LocalDateTime today = LocalDateTime.now();
        Period period = Period.between(reviewDate.toLocalDate(), today.toLocalDate());

        int daysBefore = period.getDays();

        if(daysBefore < 7)
            return daysBefore + "일 전";
        else
            return reviewDate.toLocalDate().toString();
    }

    // 동행 평가 업로드
    @Transactional
    public void uploadTourReviewProcess(TourReviewDto tourReviewDto, Member member) {
        Set<Integer> simpleTourReviews = tourReviewDto.getReviews();
        TourReview tourReview = saveTourReview(tourReviewDto, member);
        Member getMember = memberRepository.findByNickname(tourReviewDto.getNickname());

        // 간단 평가 저장
        saveSimpleReviews(simpleTourReviews, tourReview, getMember);

        boolean isAnonymous = tourReviewDto.isAnonymous();

        // 평가받은 날씨 점수
        updateReviewedWeather(tourReviewDto, getMember);

        // 평가한 사람 점수 증가
        updateWeather(member, isAnonymous);
    }

    private void updateReviewedWeather(TourReviewDto tourReviewDto, Member getMember) {
        getMember.updateWeather(tourReviewDto.getWeatherIndex());
        memberRepository.save(getMember);
    }

    private void updateWeather(Member member, boolean isAnonymous) {
        if(isAnonymous)
            member.updateWeather(1);
        else
            member.updateWeather(2);
        memberRepository.save(member);
    }

    // simple review 저장
    private void saveSimpleReviews(Set<Integer> simpleReviews, TourReview tourReview, Member getMember) {
        for (Integer simpleReview : simpleReviews) {
            int reviewIndex = (int) simpleReview;
            SimpleTourReview simpleTourReview = SimpleTourReview.builder()
                    .tourReview(tourReview)
                    .reviewIndex(reviewIndex)
                    .getMember(getMember)
                    .build();
            simpleTourReviewRepository.save(simpleTourReview);
        }
    }

    // TourReview 저장
    private TourReview saveTourReview(TourReviewDto tourReviewDto, Member sendMember) {
        Member getMember = memberRepository.findByNickname(tourReviewDto.getNickname());
        TourReview tourReview = TourReview.builder()
                .getMember(getMember)
                .sendMember(sendMember)
                .isAnonymous(tourReviewDto.isAnonymous())
                .detailReview(tourReviewDto.getDetailReview())
                .weatherIndex(tourReviewDto.getWeatherIndex())
                .reviewTime(LocalDateTime.now())
                .build();

        return tourReviewRepository.save(tourReview);
    }

}
