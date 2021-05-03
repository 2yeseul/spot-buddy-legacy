package kr.co.spotbuddy.modules.member;

import kr.co.spotbuddy.infra.config.words.Const;
import kr.co.spotbuddy.infra.domain.DeleteAccount;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.SimpleTourReview;
import kr.co.spotbuddy.infra.domain.TourReview;
import kr.co.spotbuddy.modules.confirmedTour.ConfirmedTourRepository;
import kr.co.spotbuddy.modules.deleteAccount.DeleteAccountRepository;
import kr.co.spotbuddy.modules.deleteAccount.DeleteAccountService;
import kr.co.spotbuddy.modules.deleteAccount.dto.DeleteForm;
import kr.co.spotbuddy.modules.member.dto.*;
import kr.co.spotbuddy.modules.response.APIResponse;
import kr.co.spotbuddy.modules.token.TokenInfoRepository;
import kr.co.spotbuddy.modules.tourReview.TourReviewRepository;
import kr.co.spotbuddy.modules.tourReview.TourReviewService;
import kr.co.spotbuddy.modules.tourReview.simpleTourReview.SimpleTourReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.mail.EmailMessage;
import kr.co.spotbuddy.modules.mail.EmailService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberType memberType;
    private final ConfirmedTourRepository confirmedTourRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final TourReviewRepository tourReviewRepository;
    private final SimpleTourReviewRepository simpleTourReviewRepository;
    private final TourReviewService tourReviewService;
    private final DeleteAccountService deleteAccountService;
    private final DeleteAccountRepository deleteAccountRepository;
    private final TokenInfoRepository tokenInfoRepository;


    @Transactional
    public void modifyMemberInfo(Object object, ModifyForm modifyForm) {
        Member member = memberType.getMemberType(object);
        String password = passwordEncoder.encode(modifyForm.getPassword());
        member.modifyMemberInfo(modifyForm.getNickname(), password, modifyForm.getBirth(),
                modifyForm.getGender(), modifyForm.getTeamIndex());

        memberRepository.save(member);
    }

    @Transactional
    public void deleteMemberProcess(Object object, DeleteForm deleteForm) {
        deleteAccountService.saveDeleteAccount(object, deleteForm);
        memberRepository.delete(memberType.getMemberType(object));
    }

    // TODO : 리팩토링
    public ProfileView getProfileView(Member member) {
        int tourCount = confirmedTourRepository.getMyConfirmedTours(member).size();

        return ProfileView.builder()
                .nickname(member.getNickname())
                .birth(member.getBirth())
                .teamIndex(member.getTeamIndex())
                .weather(member.getWeather())
                .gender(member.getGender())
                .confirmedTourCount(tourCount)
                .popularReviews(getPopularReviews(member))
                .latestReviews(getLatestReviews(member))
                .joinDate(member.getEmailCheckTokenGeneratedAt())
                .build();
    }

    private List<PopularDto> getPopularReviews(Member member) {
        List<PopularDto> popularDtoList = new ArrayList<>();
        if(tourReviewRepository.existsByGetMember(member)) {
            List<Integer> allPopularReviewIndexList = getPopularSimpleReviews(member);
            int totalCount = simpleTourReviewRepository.countByGetMember(member);

            for (int i = 0; i < 3; i++) {
                if(i + 1 > totalCount) break;
                int indexCount = simpleTourReviewRepository.countByGetMemberAndReviewIndex(member, allPopularReviewIndexList.get(i));
                double _ratio = (double) ((double) indexCount / (double) totalCount) * 100;
                int ratio = (int) _ratio;
                PopularDto popularDto = PopularDto.builder()
                        .reviewIndex(allPopularReviewIndexList.get(i))
                        .ratio(ratio)
                        .build();

                popularDtoList.add(popularDto);
            }
        }

        return popularDtoList;
    }

    private List<LatestDto> getLatestReviews(Member member) {
        List<LatestDto> latestDtoList = new ArrayList<>();

        if(tourReviewRepository.existsByGetMember(member)) {
            List<TourReview> tourReviews = tourReviewRepository.findAllByGetMemberOrderByIdDesc(member);

            int totalReviewSize = tourReviews.size();

            for (int i = 0; i < 3; i++) {
                if(i + 1 > totalReviewSize) break;
                String reviewDate = tourReviewService.reviewDateProcess(tourReviews.get(i).getReviewTime());
                LatestDto latestDto = LatestDto.builder()
                        .nickname(tourReviews.get(i).getSendMember().getNickname())
                        .anonymous(tourReviews.get(i).isAnonymous())
                        .review(tourReviews.get(i).getDetailReview())
                        .weatherIndex(tourReviews.get(i).getWeatherIndex())
                        .reviewDate(reviewDate)
                        .build();

                latestDtoList.add(latestDto);
            }
        }
        return latestDtoList;
    }

    // TODO : 리팩토링
    public List<Integer> getPopularSimpleReviews(Member member) {
        List<SimpleTourReview> simpleTourReviews = simpleTourReviewRepository.findAllByGetMember(member);

        Map<Integer, Integer> countMap = new HashMap<>(); for(int i=0;i<30;i++) countMap.put(i, 0);


        for (SimpleTourReview simpleTourReview : simpleTourReviews) {
            int reviewIndex = simpleTourReview.getReviewIndex();
            int previousValue = countMap.get(reviewIndex);
            countMap.replace(reviewIndex, previousValue + 1);
        }

        List<Integer> result = new ArrayList<>(countMap.keySet());
        result.sort((o1, o2) -> countMap.get(o2).compareTo(countMap.get(o1)));
        return result;
    }

    public void updateNewPassword(Object object, PasswordForm passwordForm) {
        Member member = memberType.getMemberType(object);
        member.setPassword(passwordEncoder.encode(passwordForm.getPassword()));
        memberRepository.save(member);
    }

    private boolean isBadWordsIn (String nickname) {
        for(int i = 0; i < Const.BAD_WORDS.length; i++) {
            if(nickname.contains(Const.BAD_WORDS[i])) return true;
        }
        return false;
    }

    public APIResponse nicknameCheck(String nickname) {
        APIResponse apiResponse = new APIResponse();
        if(isBadWordsIn(nickname)) {
            apiResponse.setMessage("bad");
        }
        else {
            if(memberRepository.existsByNickname(nickname)) {
                apiResponse.setMessage("no");
            }
            else {
                System.out.println(nickname);
                apiResponse.setMessage("ok");
            }
        }
        return apiResponse;
    }

    public APIResponse emailCheck(String email) {
        APIResponse apiResponse = new APIResponse();
        if(memberRepository.existsByEmail(email)) {
            apiResponse.setMessage("no");
        }
        // 탈퇴 후 10일 이내에 가입 제한
        else if(deleteAccountRepository.existsByEmail(email)) {
            LocalDateTime today = LocalDateTime.now();
            DeleteAccount deleteAccount = deleteAccountRepository.findByEmail(email);
            Period period = Period.between(deleteAccount.getDeleteTime().toLocalDate(), today.toLocalDate());

            int daysBefore = period.getDays();

            if(daysBefore < 10) apiResponse.setMessage("delete");
        }
        else apiResponse.setMessage("ok");
        return apiResponse;
    }

    @Transactional
    public String emailCheckProcess(String token, String email, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        Member member = memberRepository.findByEmail(email);
        if(member == null) return "wrong email";
        if(!member.isValidToken(token)) return "wrong token";

        // logout
        session.invalidate();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(member.getEmailCheckTokenGeneratedAt(), now);
        int durationTime = duration.toHoursPart();

        boolean isIosUser = false;

        if(tokenInfoRepository.existsByMember(member)) isIosUser = true;

        if(durationTime < 1) {
            member.completeSignUp();

            String userAgent = request.getHeader("User-Agent");

            // pc user일 때 web redirect
            if(!userAgent.contains("Mobile")) {
                response.sendRedirect("http://www.spotbuddy.co.kr/");
                return "PC User";
            }

            // ios app이 존재할 경우 app으로 열기
            if(userAgent.contains("iPhone")) {
                response.sendRedirect("spot://path/deeplink");
                return "iosUserSuccess";
            }
            response.sendRedirect("http://www.spotbuddy.co.kr/");
            return "success";
        }

        else {
            login(member);
            response.sendRedirect("http://www.spotbuddy.co.kr/api/check-email/end");
            return "expired";
        }
    }

    // 인증 메일 보내기
    public void sendSignUpConfirmEmail(EmailCheck emailCheck) {
        String token = emailCheck.getToken();
        String email = emailCheck.getEmail();

        Context context = new Context();
        
        context.setVariable("link", "/check-email-token?token="+token+"&email="+email);
        context.setVariable("host", "http://www.spotbuddy.co.kr/api");

        String message = templateEngine.process("confirm-email", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("SPOT BUDDY 가입 인증 메일입니다. (1시간 유효)")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    // 인증 메일 재전송
    @Transactional
    public void resendSignUpConfirmEmail(ResendEmail resendEmail) {
        Member member = memberRepository.findByEmail(resendEmail.getEmail());
        member.generatedEmailCheckToken();

        memberRepository.save(member);

        EmailCheck newEmailCheck = new EmailCheck();
        newEmailCheck.setEmail(member.getEmail());
        newEmailCheck.setToken(member.getEmailCheckToken());

        sendSignUpConfirmEmail(newEmailCheck);
    }

    @Transactional
    public SignUpResponse processNewMember(SignUpForm signUpForm) {
        Member newMember = saveNewMember(signUpForm);
        newMember.generatedEmailCheckToken();

        return SignUpResponse.builder()
                .token(newMember.getEmailCheckToken())
                .email(newMember.getEmail())
                .build();
    }

    private Member saveNewMember(SignUpForm signUpForm) {
        boolean isAgreeOnGetPromotion = false;
        if(signUpForm.getIsAgreeOnGetPromotion().equals("true")) isAgreeOnGetPromotion = true;

        Member member = Member.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .name(signUpForm.getName())
                .birth((signUpForm.getBirth()))
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .teamIndex(signUpForm.getTeamIndex())
                .gender(signUpForm.getGender())
                .weather(50)
                .isAgreeOnGetPromotion(isAgreeOnGetPromotion)
                .build();

        return memberRepository.save(member);
    }

    // TODO : OAuth 추가 로그인
    @Transactional
    public Member saveOauthMember(SignUpAuthForm signUpAuthForm) {
        Member member = memberRepository.findByEmail(signUpAuthForm.getEmail());
        member.setNickname(signUpAuthForm.getNickname());
        member.setName(signUpAuthForm.getName());
        member.setBirth(signUpAuthForm.getBirth());
        member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        member.setTeamIndex(signUpAuthForm.getTeamIndex());
        member.setGender(signUpAuthForm.getGender());
        member.setWeather(50);
        return memberRepository.save(member);
    }

    // 일반 로그인
    public void login(Member member) {
        if(!member.isDeleteState()) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    new UserMember(member),
                    member.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(">>>>> login username : " + username);
        Member member = new Member();

        if(memberRepository.existsByEmail(username)) {
            log.info(">>>>> " + username + " is logged on by email");
            member = memberRepository.findByEmail(username);
        }

        else if(memberRepository.existsByNickname(username)) {
            log.info(">>>>> " + username + " is logged on by nickname");
            member = memberRepository.findByNickname(username);

        }

        return new UserMember(member);
    }



    // TODO : 리팩토링
    @Transactional
    public MemberInfo getMemberInfo(Member member) {
        return MemberInfo.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .birth(member.getBirth())
                .gender(member.getGender())
                .teamIndex(member.getTeamIndex())
                .weather(member.getWeather())
                .emailVerified(member.isEmailVerified())
                .build();
    }
}
