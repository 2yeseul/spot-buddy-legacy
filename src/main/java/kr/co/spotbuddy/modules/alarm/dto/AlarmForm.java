package kr.co.spotbuddy.modules.alarm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AlarmForm {
    private int alarmType; // 1. 전체 공지 2. 동행 확정 3. 커뮤니티 댓글 4. 커뮤니티 댓글 좋아요
    private Long alarmObject;
    private String title;
    private String body;
}
