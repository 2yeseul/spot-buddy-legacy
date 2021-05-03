package kr.co.spotbuddy.modules.scheduleTour;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.scheduleTour.dto.ModifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleTourController {

    private final ScheduleTourService scheduleTourService;

    @PostMapping("/tour/modify/{tourId}")
    public void modifyTourSchedule(@CurrentUser Object object, ModifyRequest modifyRequest
            , @PathVariable Long tourId) {
        scheduleTourService.modifyTourSchedule(object, modifyRequest, tourId);
    }
}
