package kr.co.spotbuddy.modules.alarm;

import kr.co.spotbuddy.modules.alarm.dto.AlarmList;
import kr.co.spotbuddy.modules.alarm.dto.ReadStatus;
import kr.co.spotbuddy.modules.member.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    // 알람 읽음 여부
    @GetMapping("/read-status")
    public ReadStatus hasUnreadAlarm(@CurrentUser Object object) {
        return alarmService.alarmReadStatus(object);
    }

    // 나의 알람 리스트
    @GetMapping("/my-list")
    public List<AlarmList> getMyAlarmList(@CurrentUser Object object) {
        return alarmService.myAlarmList(object);
    }

    // 알람 읽음 처리
    @PostMapping("/read/{id}")
    public void setAlarmRead(@PathVariable Long id) {
        alarmService.setAlarmReadState(id);
    }

    // 개별 알람 삭제
    @PostMapping("/delete/{id}")
    public void deleteAlarm(@PathVariable Long id) {
        alarmService.deleteAlarm(id);
    }

    @PostMapping("/delete/all")
    public void deleteAllAlarms(@CurrentUser Object object) {
        alarmService.deleteAllAlarm(object);
    }

}
