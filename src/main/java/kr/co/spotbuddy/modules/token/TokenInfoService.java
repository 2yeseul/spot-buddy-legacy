package kr.co.spotbuddy.modules.token;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.TokenInfo;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.token.dto.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenInfoService {
    private final TokenInfoRepository tokenInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void iosTokenProcess(Token token) throws Exception {
        // email이 들어오지 않았을 때
        if(token.getEmail() == null) {
            TokenInfo tokenInfo = TokenInfo.builder().token(token.getToken()).build();
            tokenInfoRepository.save(tokenInfo);
            return;
        }

        Member member = memberRepository.findByEmail(token.getEmail());

        // 기존에 토큰이 db에 존재하는 경우
        if(tokenInfoRepository.existsByMember(member)) {
            TokenInfo tokenInfo = tokenInfoRepository.findByMember(member);

            // 토큰이 갱신된 경우 토큰 업데이트
            if(!tokenInfo.isEqualsWithToken(token.getToken()))
                updateNewToken(token, member);

        }

        // app 최초 접속 시, 토큰 새로 저장
        else {
            saveNewToken(token, member);
        }
    }

    private void saveNewToken(Token token, Member member) {
        TokenInfo tokenInfo = TokenInfo.builder()
                .token(token.getToken())
                .member(member)
                .build();

        tokenInfoRepository.save(tokenInfo);
    }

    private void updateNewToken(Token token, Member member) {
        TokenInfo tokenInfo = tokenInfoRepository.findByMember(member);
        tokenInfo.updateToken(token.getToken());

        tokenInfoRepository.save(tokenInfo);
    }

}
