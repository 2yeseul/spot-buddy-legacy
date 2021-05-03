package kr.co.spotbuddy.modules.message.chat;

import kr.co.spotbuddy.infra.domain.Chat;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Message;
import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.modules.block.BlockRepository;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.message.chat.dto.ChatMemberInfo;
import kr.co.spotbuddy.modules.message.chat.dto.ChatRoom;
import kr.co.spotbuddy.modules.message.chat.dto.TourInfo;
import kr.co.spotbuddy.modules.response.ChatResponse;
import kr.co.spotbuddy.modules.tour.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.message.MessageRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final TourRepository tourRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final MemberType memberType;
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;


    // 해당 채팅방의 동행 정보
    public TourInfo getTourInfoFromChat(Long id) {
        Tour tour = chatRepository.findById(id).get().getTour();
        return TourInfo.builder()
                .tourId(tour.getId())
                .nickname(tour.getMember().getNickname())
                .tourTitle(tour.getTourTitle())
                .build();
    }


    // 해당 채팅방의 유저들 정보
    public ChatMemberInfo getChatMembersInfo(Object object, Long id) {
        Chat chat = chatRepository.findById(id).get();
        Member me = memberType.getMemberType(object);

        String myNickname = me.getNickname();
        String yourNickname;

        if(me.equals(chat.getTourUploader())) yourNickname = chat.getRequestMember().getNickname();
        else yourNickname = chat.getTourUploader().getNickname();

        return ChatMemberInfo.builder()
                .myNickname(myNickname)
                .yourNickname(yourNickname)
                .build();
    }

    // 메시지 읽음 처리 및 채팅방 입장 여부 처리
    @Transactional
    public void setReadStatus(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Chat chat = chatRepository.findById(id).get();

        if(member.equals(chat.getRequestMember())) {
            chat.requestMemberState(true);
        }
        else {
            chat.tourUploaderState(true);
        }

        chatRepository.save(chat);
    }

    // 입장 설정
    public void setEnterStatus(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Chat chat = chatRepository.findById(id).get();

        if(member.equals(chat.getRequestMember())) {
            chat.setRequestMemberEnter();
        }
        else {
            chat.setTourUploaderEnter();
        }

        chatRepository.save(chat);
    }

    public void setOutStatus(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Chat chat = chatRepository.findById(id).get();

        if(member.equals(chat.getRequestMember())) {
            chat.setRequestMemberOut();
        }
        else {
            chat.setTourUploaderOut();
        }

        chatRepository.save(chat);
    }



    // 채팅방 생성
    @Transactional
    public ChatResponse createNewChatRoom(Long id, Object object) {
        Member requestMember = memberType.getMemberType(object);
        Tour tour = tourRepository.findById(id).get();
        Member tourUploader = tour.getMember();

        // 이미 채팅방이 존재 하는 경우 -> 이전 채팅방 주소
        if(chatRepository.hasChatRoom(requestMember, tourUploader, tour)) {
            return getPreviousChatRoom(requestMember, tour, tourUploader);
        }

        Chat chat = Chat.builder()
                .requestMember(requestMember)
                .tourUploader(tourUploader)
                .tour(tour)
                .build();

        Long chatId = chatRepository.save(chat).getId();

        return ChatResponse.builder()
                .chatRoomId(chatId)
                .build();
    }

    // 내 채팅방 리스트 반환
    public List<ChatRoom> getMyChatRooms(Member member) {
        List<Chat> myChatLists = chatRepository.getMyList(member);
        List<ChatRoom> chatRooms = new ArrayList<>();

        return convertToChatRoomLists(myChatLists, chatRooms, member);
    }

    // 채팅방 개별 삭제
    public void deleteMyChatRoom(Long id, Member member) {
        Chat chat = chatRepository.findById(id).get();
        if(member.equals(chat.getTourUploader())) {
            chat.deleteChatRoom("tourUploader");
        }
        else if(member.equals(chat.getRequestMember())) {
            chat.deleteChatRoom("requestMember");
        }

        chatRepository.save(chat);
    }

    // 채팅방 전체 삭제
    @Transactional
    public void deleteAllMyChatRooms(Member member) {
        List<Chat> chats = chatRepository.getMyList(member);

        for(Chat chat : chats) {
            if(member.equals(chat.getTourUploader())) {
                chat.deleteChatRoom("tourUploader");
            }
            else if(member.equals(chat.getRequestMember())) {
                chat.deleteChatRoom("requestMember");
            }

            chatRepository.save(chat);
        }
    }

    private ChatResponse getPreviousChatRoom(Member requestMember, Tour tour, Member tourUploader) {
        Chat chat = chatRepository.getChatRoom(requestMember, tourUploader, tour);
        return ChatResponse.builder()
                .chatRoomId(chat.getId())
                .build();
    }


    // DTO List로 변환
    private List<ChatRoom> convertToChatRoomLists(List<Chat> myChatLists, List<ChatRoom> chatRooms, Member member) {
        String myNickname = member.getNickname();
        for(Chat chat : myChatLists) {
            // 채팅방은 있지만 메시지가 없는 경우에는 list X
            if(messageRepository.findTopByChatOrderByIdDesc(chat) == null) continue;

            Message lastMessage = messageRepository.findTopByChatOrderByIdDesc(chat);
            String tourTitle = chat.getTour().getTourTitle();

            String lastMessageTime = parseMessageTime(lastMessage);

            String yourNickname = (myNickname.equals(chat.getRequestMember().getNickname()))
                    ? chat.getTourUploader().getNickname() : chat.getRequestMember().getNickname();
            Long chatRoomId = chat.getId();

            boolean readState = (myNickname.equals(chat.getRequestMember().getNickname())) ? chat.isRequestMemberRead()
                    : chat.isTourUploaderRead();

            Member you = memberRepository.findByNickname(yourNickname);

            // 블락한 사람의 채팅방 보여주지 않음
            if(blockRepository.isAlreadyBlocked(member, you)) continue;

            if(member.equals(chat.getRequestMember()) && chat.isRequestMemberDelete()) continue;
            if(member.equals(chat.getTourUploader()) && chat.isTourUploaderDelete()) continue;

            ChatRoom chatRoom = ChatRoom.builder()
                    .tourTitle(tourTitle)
                    .lastMessage(lastMessage.getMessage())
                    .lastMessageTime(lastMessageTime)
                    .chatRoomId(chatRoomId)
                    .tourId(chat.getTour().getId())
                    .nickname(yourNickname)
                    .readState(readState)
                    .lastMessageId(lastMessage.getId())
                    .build();

            chatRooms.add(chatRoom);
        }

        chatRooms.sort(Comparator.comparing(ChatRoom::getLastMessageId).reversed());

        return chatRooms;
    }

    private String parseMessageTime(Message lastMessage) {
        String lastMessageTime = lastMessage.getMessageTime();
        String thisYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        String month = lastMessageTime.substring(0, lastMessageTime.lastIndexOf("/"));

        int slashIndex = lastMessageTime.lastIndexOf("/");

        int dayLength = lastMessageTime.substring(0, lastMessageTime.lastIndexOf(" ")).length();

        String day = lastMessageTime.substring(slashIndex + 1, slashIndex + 3);

        String dayOfWeek = lastMessageTime.substring(slashIndex + 3, slashIndex + 4);

        // 10일 미만일 때
        if(dayLength == 4) {
            day = "0" + lastMessageTime.substring(slashIndex + 1, slashIndex + 2);
            dayOfWeek = lastMessageTime.substring(slashIndex + 2, slashIndex + 3);
        }

        String hour = lastMessageTime.substring(lastMessageTime.lastIndexOf(" ") + 1, lastMessageTime.lastIndexOf(":"));
        if(hour.length() == 1) hour = "0" + hour;

        String min = lastMessageTime.substring(lastMessageTime.lastIndexOf(":"));

        if(month.length() < 2) month = "0" + month;

        return thisYear + "-" + month + "-" + day + " " + dayOfWeek + " " + hour + min;
    }

}