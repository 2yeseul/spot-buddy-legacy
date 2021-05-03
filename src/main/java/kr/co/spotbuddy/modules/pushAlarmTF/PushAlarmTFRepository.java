package kr.co.spotbuddy.modules.pushAlarmTF;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.PushAlarmTF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushAlarmTFRepository extends JpaRepository<PushAlarmTF, Long> {
    boolean existsByMember(Member member);
    PushAlarmTF findByMember(Member member);
}
