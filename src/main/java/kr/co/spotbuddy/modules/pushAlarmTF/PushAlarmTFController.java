package kr.co.spotbuddy.modules.pushAlarmTF;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.pushAlarmTF.dto.MyAlarmStatus;
import kr.co.spotbuddy.modules.pushAlarmTF.dto.PushAlarmTypeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push-alarm")
public class PushAlarmTFController {

    private final PushAlarmTFService pushAlarmTFService;

    @PostMapping("/setting")
    public void setPushAlarmStatus(@CurrentUser Object object, @RequestBody PushAlarmTypeStatus pushAlarmTypeStatus) {
        pushAlarmTFService.setPushAlarmStatus(object, pushAlarmTypeStatus);
    }

    @GetMapping("/my-status")
    public MyAlarmStatus getMyAlarmStatus(@CurrentUser Object object) {
        return pushAlarmTFService.getMyPushAlarmStatus(object);
    }
}
