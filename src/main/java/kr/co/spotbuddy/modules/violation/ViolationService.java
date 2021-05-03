package kr.co.spotbuddy.modules.violation;

import kr.co.spotbuddy.infra.domain.*;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.posts.PostsRepository;
import kr.co.spotbuddy.modules.tour.TourRepository;
import kr.co.spotbuddy.modules.violation.dto.MemberViolationDto;
import kr.co.spotbuddy.modules.violation.dto.PostViolationDto;
import kr.co.spotbuddy.modules.violation.dto.TourViolationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.comments.CommentsRepository;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final MemberType memberType;
    private final TourRepository tourRepository;
    private final ViolationRepository violationRepository;
    private final MemberRepository memberRepository;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;


    // 동행 글 신고
    @Transactional
    public void saveNewReportedTourViolation(Object object, Long tourId, TourViolationDto tourViolationDto) {
        Member doReport = memberType.getMemberType(object);
        Tour violatedTour = tourRepository.findById(tourId).get();
        Member doViolate = violatedTour.getMember();
        int violationIndex = tourViolationDto.getViolationIndex();

        String etc = getTourViolationEtc(tourViolationDto);
        saveTourViolation(doReport, violatedTour, doViolate, violationIndex, etc);
    }

    // 사용자 신고
    @Transactional
    public void saveNewReportedMemberViolation(Object object, MemberViolationDto memberViolationDto) {
        Member doReport = memberType.getMemberType(object);
        Member doViolate = memberRepository.findByNickname(memberViolationDto.getNickname());

        String etc = getMemberViolationEtc(memberViolationDto);
        saveMemberViolation(memberViolationDto, doReport, doViolate, etc);
    }

    // 커뮤니티 글 신고
    @Transactional
    public void saveNewReportedPostViolation(Object object, Long postId, PostViolationDto postViolationDto) {
        Posts violatedPost = postsRepository.findById(postId).get();
        Member doReport = memberType.getMemberType(object);
        Member doViolate = violatedPost.getMember();

        String etc = getPostViolationEtc(postViolationDto);
        savePostViolation(postViolationDto, violatedPost, doReport, doViolate, etc);
    }

    @Transactional
    public void saveNewReportedCommentViolation(Object object, Long commentId, PostViolationDto postViolationDto) {
        Comments comments = commentsRepository.findById(commentId).get();
        Member doReport = memberType.getMemberType(object);
        Member doViolate = comments.getMember();

        String etc = getPostViolationEtc(postViolationDto);
        saveCommentViolation(postViolationDto, comments, doReport, doViolate, etc);
    }

    private void saveCommentViolation(PostViolationDto postViolationDto, Comments comments, Member doReport, Member doViolate, String etc) {
        Violation violation = Violation.builder()
                .doReport(doReport)
                .doViolate(doViolate)
                .comments(comments)
                .isFromComment(true)
                .violateIndex(postViolationDto.getViolationIndex())
                .etc(etc)
                .build();

        violationRepository.save(violation);
    }

    private void savePostViolation(PostViolationDto postViolationDto, Posts violatedPost, Member doReport, Member doViolate, String etc) {
        Violation violation = Violation.builder()
                .doReport(doReport)
                .doViolate(doViolate)
                .posts(violatedPost)
                .isFromCommunity(true)
                .violateIndex(postViolationDto.getViolationIndex())
                .etc(etc)
                .build();

        violationRepository.save(violation);
    }

    // TODO : 리팩토링
    private String getPostViolationEtc(PostViolationDto postViolationDto) {
        String etc;

        if(postViolationDto.getEtc() == null || postViolationDto.getEtc().equals("")) etc = "empty";
        else etc = postViolationDto.getEtc();

        return etc;
    }

    // TODO : 리팩토링 -> 제네릭 처리
    private String getMemberViolationEtc(MemberViolationDto memberViolationDto) {
        String etc;

        if(memberViolationDto.getEtc() == null || memberViolationDto.getEtc().equals("")) etc = "empty";
        else etc = memberViolationDto.getEtc();
        return etc;
    }
    // TODO : 리팩토링 -> 제네릭 처리
    private String getTourViolationEtc(TourViolationDto tourViolationDto) {
        String etc;

        if(tourViolationDto.getEtc() == null || tourViolationDto.getEtc().equals("")) etc = "empty";
        else etc = tourViolationDto.getEtc();
        return etc;
    }

    private void saveTourViolation(Member doReport, Tour violatedTour, Member doViolate, int violationIndex, String etc) {
        Violation violation = Violation.builder()
                .doReport(doReport)
                .doViolate(doViolate)
                .violatedTour(violatedTour)
                .violateIndex(violationIndex)
                .etc(etc)
                .isFromTour(true)
                .build();

        violationRepository.save(violation);
    }

    private void saveMemberViolation(MemberViolationDto memberViolationDto, Member doReport, Member doViolate, String etc) {
        Violation violation = Violation.builder()
                .doViolate(doViolate)
                .doReport(doReport)
                .violateIndex(memberViolationDto.getViolationIndex())
                .etc(etc)
                .isFromTour(false)
                .build();

        violationRepository.save(violation);
    }

}
