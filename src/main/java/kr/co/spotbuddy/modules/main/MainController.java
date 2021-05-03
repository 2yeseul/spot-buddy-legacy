package kr.co.spotbuddy.modules.main;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberService;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.member.dto.MemberInfo;
import kr.co.spotbuddy.modules.response.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MemberRepository memberRepository;
    private final MemberType memberType;
    private final MemberService memberService;

    @GetMapping("/device")
    public String getUserAgent(@RequestHeader(value = "User-Agent") String header) {
        log.info("USERS AGENT IS : " + header);
        return header;
    }

    @GetMapping("/a")
    public APIResponse _main(Authentication authentication) {
        APIResponse apiResponse = new APIResponse();
        if(authentication == null) {
            apiResponse.setMessage("anonymous");
        }

        else if(memberRepository.existsByNickname(authentication.getName())) {
            apiResponse.setMessage("일반 로그인 - " + authentication.getName());
        }
        else {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            apiResponse.setMessage("SNS 로그인 - " + oAuth2User.getAttribute("email"));
        }

        return apiResponse;
    }

    @PostMapping("/users")
    public MemberInfo nowMember(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return memberService.getMemberInfo(member);
    }


    @PostMapping("/remember-me-token")
    public String isPersistentLogin(@CookieValue(value = "rememberMe") Cookie rememberMeCookie) {
        return rememberMeCookie.getValue();
    }

    @PostMapping("/login-session")
    public String isJSession(@CookieValue(value = "JSESSIONID") Cookie JSession) {
        return JSession.getValue();
    }

}
