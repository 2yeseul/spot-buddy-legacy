package kr.co.spotbuddy.modules.pushAlarmTF;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.PushAlarmTF;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.pushAlarmTF.dto.ALARM_TYPE;
import kr.co.spotbuddy.modules.pushAlarmTF.dto.MyAlarmStatus;
import kr.co.spotbuddy.modules.pushAlarmTF.dto.PushAlarmTypeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PushAlarmTFService {

    private final PushAlarmTFRepository pushAlarmTFRepository;
    private final MemberType memberType;

    @Transactional
    public void setPushAlarmStatus(Object object, PushAlarmTypeStatus pushAlarmStatus) {
        Member member = memberType.getMemberType(object);
        ALARM_TYPE alarm_type = ALARM_TYPE.valueOf(pushAlarmStatus.getPushAlarmType());

        if(pushAlarmTFRepository.existsByMember(member)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(member);
            alarm_type.changeAlarmStatus(pushAlarmTF, pushAlarmStatus);

            pushAlarmTFRepository.save(pushAlarmTF);
        }

        else {
            PushAlarmTF pushAlarmTF = new PushAlarmTF();
            pushAlarmTF.setMember(member);
            alarm_type.changeAlarmStatus(pushAlarmTF, pushAlarmStatus);

            pushAlarmTFRepository.save(pushAlarmTF);
        }
    }

    public MyAlarmStatus getMyPushAlarmStatus(Object object) {
        Member member = memberType.getMemberType(object);
        if (pushAlarmTFRepository.existsByMember(member)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(member);

            return MyAlarmStatus.builder()
                    .messageTurnOn(pushAlarmTF.isMessageTurnOn())
                    .activityTurnOn(pushAlarmTF.isActivityTurnOn())
                    .scheduleTurnOn(pushAlarmTF.isScheduleTurnOn())
                    .promoTurnOn(pushAlarmTF.isPromoTurnOn())
                    .build();
        }

        // 설정이 존재하지 않을 때, push alarm 모두 true
        else {
            return MyAlarmStatus.builder()
                    .messageTurnOn(true)
                    .activityTurnOn(true)
                    .scheduleTurnOn(true)
                    .promoTurnOn(true)
                    .build();
        }
    }
}
