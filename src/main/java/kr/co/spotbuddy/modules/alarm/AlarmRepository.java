package kr.co.spotbuddy.modules.alarm;

import kr.co.spotbuddy.infra.domain.Alarm;
import kr.co.spotbuddy.infra.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByMemberOrderByIdDesc(Member member);
    boolean existsByMember(Member member);
    boolean existsByMemberAndReadStatus(Member member, boolean readStatus);

    void deleteAllByMember(Member member);

    default boolean isMemberReadActivityAlarm(Member member) {
        return !existsByMemberAndReadStatus(member, false);
    }
}
