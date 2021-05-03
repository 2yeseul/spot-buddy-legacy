package kr.co.spotbuddy.modules.alarm;

import kr.co.spotbuddy.infra.domain.Alarm;
import kr.co.spotbuddy.infra.domain.Chat;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Posts;
import kr.co.spotbuddy.modules.alarm.dto.AlarmForm;
import kr.co.spotbuddy.modules.alarm.dto.AlarmList;
import kr.co.spotbuddy.modules.alarm.dto.ReadStatus;
import kr.co.spotbuddy.modules.block.BlockRepository;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.posts.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.message.MessageRepository;
import kr.co.spotbuddy.modules.message.chat.ChatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final MemberType memberType;
    private final ChatRepository chatRepository;
    private final PostsRepository postsRepository;
    private final MessageRepository messageRepository;
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    // 알람 전체 삭제
    @Transactional
    public void deleteAllAlarm(Object object) {
        Member member = memberType.getMemberType(object);
        alarmRepository.deleteAllByMember(member);
    }

    // 알람 삭제
    @Transactional
    public void deleteAlarm(Long id) {
        Alarm alarm = alarmRepository.findById(id).get();
        alarmRepository.delete(alarm);
    }

    // 읽지 않은 알람 존재 여부
    public ReadStatus alarmReadStatus(Object object) {
        Member member = memberType.getMemberType(object);
        // 읽지 않은 활동 알람 존재 여부
        boolean activityAlarmReadStatus = alarmRepository.isMemberReadActivityAlarm(member);

        // 활동 알람이 존재하는지 검증
        if(alarmRepository.existsByMember(member)) {
            // 읽지 않은 활동 알람들이 있을 때
            if(!activityAlarmReadStatus) {
                return ReadStatus.builder()
                        .alarmRead(false)
                        .build();
            }
        }

        // 채팅을 한 이력이 한 번 이라도 있는지
        if(chatRepository.existsByRequestMemberOrTourUploader(member, member)) {
            List<Chat> myChatLists = chatRepository.getMyList(member);
            String myNickname = member.getNickname();

            if (isChatAllRead(member, myChatLists, myNickname)) {
                return ReadStatus.builder()
                        .alarmRead(false)
                        .build();
            }
        }

        return ReadStatus.builder()
                .alarmRead(true)
                .build();
    }

    private boolean isChatAllRead(Member member, List<Chat> myChatLists, String myNickname) {
        for(Chat chat : myChatLists) {
            // 채팅방은 있지만 메시지는 없는 상황(요청자가 메시지를 보내지 않은 상황) 일 때는 continue
            if(messageRepository.findTopByChatOrderByIdDesc(chat) == null) continue;

            String yourNickname = (myNickname.equals(chat.getRequestMember().getNickname()))
                    ? chat.getTourUploader().getNickname() : chat.getRequestMember().getNickname();

            Member you = memberRepository.findByNickname(yourNickname);

            // 블락한 사람으로부터의 메시지나 채팅이 있을 땐 continue
            if(blockRepository.isAlreadyBlocked(member, you)) continue;

            // 채팅방을 삭제한 상황일 때 delete
            if(member.equals(chat.getRequestMember()) && chat.isRequestMemberDelete()) continue;
            if(member.equals(chat.getTourUploader()) && chat.isTourUploaderDelete()) continue;

            boolean readState = (myNickname.equals(chat.getRequestMember().getNickname())) ? chat.isRequestMemberRead()
                    : chat.isTourUploaderRead();

            if(!readState) return true;

        }
        return false;
    }

    // 새 활동알람 저장
    @Transactional
    public void saveAlarmProcess(AlarmForm alarmForm, Member member) {
        Alarm alarm = Alarm.builder()
                .alarmType(alarmForm.getAlarmType())
                .alarmedObject(alarmForm.getAlarmObject())
                .readStatus(false)
                .title(alarmForm.getTitle())
                .body(alarmForm.getBody())
                .member(member)
                .alarmDate(LocalDateTime.now())
                .build();

        alarmRepository.save(alarm);
    }

    // 알람 읽음 여부 처리
    @Transactional
    public void setAlarmReadState(Long id) {
        Alarm alarm = alarmRepository.findById(id).get();
        alarm.readTrue();

        alarmRepository.save(alarm);
    }

    // 나의 활동 알람 리스트
    public List<AlarmList> myAlarmList(Object object) {
        Member member = memberType.getMemberType(object);

        // 알람이 존재할 때
        if(alarmRepository.existsByMember(member)) {
            List<Alarm> alarms = alarmRepository.findAllByMemberOrderByIdDesc(member);
            return convertToAlarmList(alarms);
        }
        else return null;
    }

    // 알람 리스트로 변화
    @NotNull
    private List<AlarmList> convertToAlarmList(List<Alarm> alarms) {
        List<AlarmList> alarmLists = new ArrayList<>();
        LocalDateTime alarmDate = LocalDateTime.now();

        for(Alarm alarm : alarms) {
            int teamIndex = -1;
            if(alarm.getAlarmDate()!=null) alarmDate = alarm.getAlarmDate();
            if(alarm.getAlarmType() == 3 || alarm.getAlarmType() == 4) {
                Posts posts = postsRepository.findById(alarm.getAlarmedObject()).get();
                teamIndex = posts.getTeamIndex();
            }

            AlarmList alarmList = AlarmList.builder()
                    .alarmId(alarm.getId())
                    .alarmType(alarm.getAlarmType())
                    .alarmedObjectId(alarm.getAlarmedObject())
                    .readStatus(alarm.isReadStatus())
                    .title(alarm.getTitle())
                    .body(alarm.getBody())
                    .alarmDate(alarmDate)
                    .teamIndex(teamIndex)
                    .build();

            alarmLists.add(alarmList);
        }
        return alarmLists;
    }

}
