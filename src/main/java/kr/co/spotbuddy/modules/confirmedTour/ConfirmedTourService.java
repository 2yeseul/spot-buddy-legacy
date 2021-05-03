package kr.co.spotbuddy.modules.confirmedTour;

import kr.co.spotbuddy.infra.domain.*;
import kr.co.spotbuddy.modules.confirmedTour.dto.CancelResponse;
import kr.co.spotbuddy.modules.confirmedTour.dto.CancelTour;
import kr.co.spotbuddy.modules.confirmedTour.dto.ChatRoomIdDto;
import kr.co.spotbuddy.modules.confirmedTour.dto.ConfirmedMember;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.token.TokenInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.alarm.AlarmService;
import kr.co.spotbuddy.modules.alarm.dto.AlarmForm;
import kr.co.spotbuddy.infra.firebase.FirebaseCloudMessageService;
import kr.co.spotbuddy.modules.message.chat.ChatRepository;
import kr.co.spotbuddy.modules.pushAlarmTF.PushAlarmTFRepository;
import kr.co.spotbuddy.modules.response.ConfirmTourResponse;
import kr.co.spotbuddy.modules.tour.TourRepository;
import kr.co.spotbuddy.modules.tour.TourService;
import kr.co.spotbuddy.modules.tour.dto.TourList;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ConfirmedTourService {

    private final MemberType memberType;
    private final ConfirmedTourRepository confirmedTourRepository;
    private final TourService tourService;
    private final TourRepository tourRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final TokenInfoRepository tokenInfoRepository;
    private final AlarmService alarmService;
    private final PushAlarmTFRepository pushAlarmTFRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    // 동행 확정 여부
    public ConfirmTourResponse isConfirmedChat(Long id, Object object) {
        Member member = memberType.getMemberType(object);

        boolean isMemberConfirm = confirmedTourRepository.existsByChatIdAndMember(id, member);

        return ConfirmTourResponse.builder().isConfirmed(isMemberConfirm).build();
    }

    // 동행 취소
    public CancelResponse isCanceledTour(Object object, Long id) {
        Tour tour = tourRepository.findById(id).get();
        Member member = memberType.getMemberType(object);

        boolean canceled = confirmedTourRepository.isMemberCanceled(member, tour);

        return CancelResponse.builder().canceled(canceled).build();
    }

    // 내 동행 취소 리스트
    public List<CancelTour> myCancelTourList(Object object) {
        Member member = memberType.getMemberType(object);
        List<ConfirmedTour> confirmedTours = confirmedTourRepository.findAllByMemberAndDeleteStateOrderById(member, true);

        List<CancelTour> cancelTours = new ArrayList<>();

        for(ConfirmedTour confirmedTour : confirmedTours) {
            CancelTour cancelTour = CancelTour.builder()
                    .id(confirmedTour.getTour().getId())
                    .build();

            cancelTours.add(cancelTour);
        }

        return cancelTours;
    }

    // 동행 확정 여부
    public ConfirmTourResponse isUserConfirmedTour(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Tour tour = tourRepository.findById(id).get();
        boolean isConfirmed = confirmedTourRepository.existsByTourAndMember(tour, member);

        return ConfirmTourResponse.builder()
                .isConfirmed(isConfirmed)
                .build();
    }


    public ConfirmTourResponse isAllConfirmedTour(Long id) {
        Chat chat = chatRepository.findById(id).get();
        boolean allConfirmed = confirmedTourRepository.isAllConfirmed(chat.getTourUploader(), chat.getRequestMember(), id);

        return ConfirmTourResponse.builder()
                .isConfirmed(allConfirmed)
                .build();
    }

    // 동행 확정
    @Transactional
    public void confirmNewTour(Object object, Long id, ChatRoomIdDto chatRoomIdDto) throws Exception {
        Tour tour = tourRepository.findById(id).get();
        Chat chat = chatRepository.findById(chatRoomIdDto.getChatRoomId()).get();

        Member member = memberType.getMemberType(object);

        // 현재 확정 인원 업데이트 -> 확정한 사람이 동행 모집자가 아닐때만 해줘야함
        if(!member.equals(tour.getMember()))
            tour.updateNowMemberCount();

        // 이미 확정된 사용자가 아닐 때만
        saveConfirmMember(member, tour, chat);

        // 모든 사용자가 확정했을 때 push alarm 보내줘야함
        pushAlarmProcess(chat);
    }

    private void saveConfirmMember(Member member, Tour tour, Chat chat) {
        if(!confirmedTourRepository.existsByChatIdAndMember(chat.getId(), member)) {
            ConfirmedTour requestConfirmed = ConfirmedTour.builder()
                    .member(member)
                    .tour(tour)
                    .chatId(chat.getId())
                    .build();

            confirmedTourRepository.save(requestConfirmed);
        }
    }

    // 내가 확정한 동행 목록
    public List<TourList> getMyConfirmedTourList(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        List<Tour> tours = getMyConfirmTours(member);
        return tourService.makePersonalTourList(tours);
    }


    // 내 모집 동행에서의 확정된 사용자 목록
    public ConfirmedMember getConfirmedMemberList(Long id, Member member) {
        Tour tour = tourRepository.findById(id).get();
        List<ConfirmedTour> confirmedTours = confirmedTourRepository.findAllByTour(tour);
        List<String> nicknames = new ArrayList<>();
        String myNickname = member.getNickname();

        // 내 닉네임 부터 추가해주기
        nicknames.add(myNickname);

        for (ConfirmedTour confirmedTour : confirmedTours) {
            if(confirmedTour.isDeleteState()) continue;
            String nickname = confirmedTour.getMember().getNickname();
            if(nickname.equals(myNickname) || nicknames.contains(nickname)) continue;;
            nicknames.add(nickname);
        }

        return confirmedMemberList(tour, nicknames);
    }


    // 동행 확정 취소
    @Transactional
    public void cancelTour(Object object, Long tourId) {
        Member member = memberType.getMemberType(object);
        Tour tour = tourRepository.findById(tourId).get();

        ConfirmedTour confirmedTour = confirmedTourRepository.findByTourAndMember(tour, member);
        cancelConfirmedTour(member, tour, confirmedTour);
    }

    private List<Tour> getMyConfirmTours(Member member) {
        List<ConfirmedTour> confirmedTours = confirmedTourRepository.getMyConfirmedTours(member);
        List<Tour> tours = new ArrayList<>();

        for(ConfirmedTour confirmedTour : confirmedTours) {
            if(!confirmedTour.isDeleteState()) {
                if(!chatRepository.existsById(confirmedTour.getChatId())) continue;
                Chat chat = chatRepository.findById(confirmedTour.getChatId()).get();
                if(confirmedTourRepository.isAllConfirmed(chat.getTourUploader(), chat.getRequestMember(), confirmedTour.getChatId())) {
                    if(!tours.contains(confirmedTour.getTour()))
                        tours.add(confirmedTour.getTour());
                }
            }
        }
        return tours;
    }

    private ConfirmedMember confirmedMemberList(Tour tour, List<String> nicknames) {
        return ConfirmedMember.builder()
                .tourLocation(tour.getTourLocation())
                .tourTeam(tour.getTourTeam())
                .tourTitle(tour.getTourTitle())
                .tourStartDate(tour.getStartDate())
                .tourEndDate(tour.getEndDate())
                .nicknames(nicknames)
                .build();
    }

    private void cancelConfirmedTour(Member member, Tour tour, ConfirmedTour confirmedTour) {
        if(confirmedTourRepository.existsByTourAndMember(tour, member)) {
            confirmedTour.cancelConfirm();
            // 확정 멤버수 discount
            tour.discountNowMemberCount();

            tourRepository.save(tour);
        }
    }

    private void saveAlarm(Chat chat) {
        Long alarmObject = chat.getTour().getId();
        int alarmType = 2;
        String title = chat.getTour().getTourTitle();
        String uploaderNickname = chat.getTourUploader().getNickname();

        // 닉네임이 6글자 보다 길 때, 말줄임표 적용
        uploaderNickname = makeNicknameShort(uploaderNickname);

        String body = "님과의 동행이 확정되었습니다.";

        AlarmForm requestForm = AlarmForm.builder()
                .alarmType(alarmType)
                .alarmObject(alarmObject)
                .title(title)
                .body(uploaderNickname + body)
                .build();

        String requestNickname = chat.getRequestMember().getNickname();

        requestNickname = makeNicknameShort(requestNickname);

        AlarmForm uploaderForm = AlarmForm.builder()
                .alarmType(alarmType)
                .alarmObject(alarmObject)
                .title(title)
                .body(requestNickname + body)
                .build();

        // TODO : 차단 당했을 때 알람은 ?
        alarmService.saveAlarmProcess(requestForm, chat.getRequestMember());
        alarmService.saveAlarmProcess(uploaderForm, chat.getTourUploader());
    }

    private String makeNicknameShort(String nickname) {
        if(nickname.length() > 6) {
            nickname = nickname.substring(0, 6) + "...";
        }
        return nickname;
    }

    private void pushAlarmProcess(Chat chat) throws Exception {
        Member requestMember = chat.getRequestMember();
        Member tourUploader = chat.getTourUploader();

        boolean requestTurnOn = false;
        boolean uploaderTurnOn = false;

        if(pushAlarmTFRepository.existsByMember(requestMember)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(requestMember);
            requestTurnOn = pushAlarmTF.isActivityTurnOn();
        }

        // push alarm 설정 이력이 없을 때 -> default : true
        if(!pushAlarmTFRepository.existsByMember(requestMember)) requestTurnOn = true;

        if(pushAlarmTFRepository.existsByMember(tourUploader)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(tourUploader);
            uploaderTurnOn = pushAlarmTF.isActivityTurnOn();
        }

        // push alarm 설정 이력이 없을 때 -> default : true
        if(!pushAlarmTFRepository.existsByMember(tourUploader)) uploaderTurnOn = true;

        if(confirmedTourRepository.isAllConfirmed(tourUploader, requestMember, chat.getId())) {
            // 알람 저장
            saveAlarm(chat);

            if (tokenInfoRepository.existsByMember(requestMember) && requestTurnOn) {
                pushAlarmToConfirmedMember(requestMember, tourUploader.getNickname(), chat);
            }

            if (tokenInfoRepository.existsByMember(tourUploader) && uploaderTurnOn) {
                pushAlarmToConfirmedMember(tourUploader, requestMember.getNickname(), chat);
            }

        }
    }


    private void pushAlarmToConfirmedMember(Member member, String nickname, Chat chat) throws Exception {
        boolean activityAlarm = false;

        if(pushAlarmTFRepository.existsByMember(member)) activityAlarm = true;

        if(activityAlarm || !pushAlarmTFRepository.existsByMember(member)) {
            if (tokenInfoRepository.existsByMember(member)) {
                String token = tokenInfoRepository.findByMember(member).getToken();
                String title = chat.getTour().getTourTitle();
                String body = nickname + "님과의 동행이 확정되었습니다.";
                String path = "http://3.35.213.43/profile/my_join_company_list";

                firebaseCloudMessageService.sendMessageTo(token, title, body, path);
            }
        }
    }

}
