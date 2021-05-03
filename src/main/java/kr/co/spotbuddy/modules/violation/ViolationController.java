package kr.co.spotbuddy.modules.violation;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.violation.dto.MemberViolationDto;
import kr.co.spotbuddy.modules.violation.dto.PostViolationDto;
import kr.co.spotbuddy.modules.violation.dto.TourViolationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ViolationController {
    private final ViolationService violationService;

    // 동행 글 신고
    @PostMapping("/report-tour-violate/{tourId}")
    public void reportTourViolation(@CurrentUser Object object, @PathVariable Long tourId
            , @RequestBody TourViolationDto tourViolationDto) {
        violationService.saveNewReportedTourViolation(object, tourId, tourViolationDto);
    }

    // 사용자 신고
    @PostMapping("/report-member-violate")
    public void reportMemberViolation(@CurrentUser Object object, @RequestBody MemberViolationDto memberViolationDto) {
        violationService.saveNewReportedMemberViolation(object, memberViolationDto);
    }

    // 커뮤니티 글 신고
    @PostMapping("/report-post-violate/{postId}")
    public void reportCommunityViolation(@CurrentUser Object object, @PathVariable Long postId, @RequestBody PostViolationDto postViolationDto) {
        violationService.saveNewReportedPostViolation(object, postId, postViolationDto);
    }

    // 댓글 신고
    @PostMapping("/report-comment-violate/{commentId}")
    public void reportCommentViolation(@CurrentUser Object object, @PathVariable Long commentId, @RequestBody PostViolationDto postViolationDto) {
        violationService.saveNewReportedCommentViolation(object, commentId, postViolationDto);
    }

}
