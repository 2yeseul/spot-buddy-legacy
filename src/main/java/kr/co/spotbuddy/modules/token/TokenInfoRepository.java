package kr.co.spotbuddy.modules.token;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenInfoRepository extends JpaRepository<TokenInfo, Long> {
    TokenInfo findByMember(Member member);
    boolean existsByMember(Member member);
}
