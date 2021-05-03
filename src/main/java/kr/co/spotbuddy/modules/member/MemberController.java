package kr.co.spotbuddy.modules.member;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.deleteAccount.dto.DeleteForm;
import kr.co.spotbuddy.modules.member.dto.*;
import kr.co.spotbuddy.modules.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final SignUpFormValidator signUpFormValidator;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final MemberType memberType;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }


    // 회원가입
    @PostMapping("/sign-up")
    public SignUpResponse signUpForm(@Valid @RequestBody SignUpForm signUpForm, Errors errors) {
        return memberService.processNewMember(signUpForm);
    }

    // 닉네임 유효성 검사
    @PostMapping("/valid-nickname")
    public APIResponse isValidNickname(@RequestBody ValidMember validMember) {
        String nickname = validMember.getNickname();
        return memberService.nicknameCheck(nickname);
    }

    // 이메일 유효성 검사
    @PostMapping("/valid-email")
    public APIResponse isValidEmail(@RequestBody ValidMember validMember) {
        String email = validMember.getEmail();
        return memberService.emailCheck(email);
    }

    // TODO : 리팩토링
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        return memberService.emailCheckProcess(token, email, request, response, session);
    }

    // 이메일 만료 조회
    @GetMapping("/check-email/end")
    public APIResponse emailCheckTimeEnd() {
        APIResponse response = new APIResponse();
        response.setMessage("expired");
        return response;
    }

    @PostMapping("/sign-up-oauth")
    public Member signUpOauth(Authentication authentication, @RequestBody SignUpAuthForm signUpAuthForm) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        signUpAuthForm.setEmail(oAuth2User.getAttribute("email"));
        return memberService.saveOauthMember(signUpAuthForm);
    }

    // 나의 프로필 조회
    @GetMapping("/my-profile")
    public ProfileView myProfile(@CurrentUser Object object) {
        return memberService.getProfileView(memberType.getMemberType(object));
    }

    // 상대방 프로필 조회
    @PostMapping("/your-profile")
    public ProfileView yourProfile(@RequestBody YourProfileView yourProfileView, HttpServletRequest request) {
        String nickname = yourProfileView.getNickname();
        String userAgent = request.getHeader("User-Agent");
        log.info("### user agent is " + userAgent);
        return memberService.getProfileView(memberRepository.findByNickname(nickname));
    }

    // 회원가입 시 메일 보내기
    @PostMapping("/send-email")
    public void sendConfirmMail(@RequestBody EmailCheck emailCheck) {
        memberService.sendSignUpConfirmEmail(emailCheck);
    }

    // 이메일 재전송
    @PostMapping("/resend-email")
    public void resendConfirmMail(@RequestBody ResendEmail resendEmail) {
        memberService.resendSignUpConfirmEmail(resendEmail);
    }

    // 간단 평가 조회
    @GetMapping("/my-simple-reviews")
    public List<Integer> myPopularSimpleReviews(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return memberService.getPopularSimpleReviews(member);
    }

    // 탈퇴
    @PostMapping("/delete-member")
    public void deleteMember(@CurrentUser Object object, @RequestBody DeleteForm deleteForm) {
        memberService.deleteMemberProcess(object, deleteForm);
    }

}
