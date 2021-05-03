package kr.co.spotbuddy.modules.member;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.spotbuddy.infra.domain.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByTeamIndex(int index);

    Member findByNickname(String nickname);

    Member findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    int countByTeamIndex(int teamIndex);

}
