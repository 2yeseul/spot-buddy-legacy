package kr.co.spotbuddy.modules.scheduleTour;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.ScheduleTour;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.scheduleTour.dto.ModifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleTourService {
    private final MemberType memberType;
    private final ScheduleTourRepository scheduleTourRepository;

    // 일정 - 동행 일정 수정
    @Transactional
    public void modifyTourSchedule(Object object, ModifyRequest modifyRequest, Long tourId) {
        Member member = memberType.getMemberType(object);

        // 수정 기록이 있을 때
        if(scheduleTourRepository.existsByMemberIdAndTourId(member.getId(), tourId)) {
            modifyExistsContent(modifyRequest, tourId, member);
        }
        // 수정 기록이 없을 때
        else {
            saveNewContent(modifyRequest, tourId, member);
        }
    }

    private void saveNewContent(ModifyRequest modifyRequest, Long tourId, Member member) {
        ScheduleTour scheduleTour = ScheduleTour.builder()
                .memberId(member.getId())
                .startTime(modifyRequest.getStartTime())
                .endTime(modifyRequest.getEndTime())
                .tourId(tourId)
                .memo(modifyRequest.getMemo())
                .build();

        scheduleTourRepository.save(scheduleTour);
    }

    private void modifyExistsContent(ModifyRequest modifyRequest, Long tourId, Member member) {
        ScheduleTour scheduleTour = scheduleTourRepository.findByMemberIdAndTourId(member.getId(), tourId);
        scheduleTour.modifyScheduleContent(modifyRequest);

        scheduleTourRepository.save(scheduleTour);
    }
}
