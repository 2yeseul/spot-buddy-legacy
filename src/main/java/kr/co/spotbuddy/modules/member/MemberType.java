package kr.co.spotbuddy.modules.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import kr.co.spotbuddy.infra.domain.Member;

@Component
@RequiredArgsConstructor
public class MemberType {

    private final MemberRepository memberRepository;

    public Member getMemberType(Object object) {
        if(object.getClass().equals(DefaultOAuth2User.class)) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) object;
            return memberRepository.findByEmail(oAuth2User.getAttribute("email"));
        }

        else if(object.getClass().equals(UserMember.class)) {
            return ((UserMember) object).getMember();
        }

        else return new Member();
    }
}
