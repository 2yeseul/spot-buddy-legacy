package kr.co.spotbuddy.modules.message.chat;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.message.chat.dto.ChatMemberInfo;
import kr.co.spotbuddy.modules.message.chat.dto.ChatRoom;
import kr.co.spotbuddy.modules.message.chat.dto.TourInfo;
import kr.co.spotbuddy.modules.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberType memberType;

    // 채팅방 입장
    @PostMapping("/enter/{id}")
    public void setEnterStatus(@CurrentUser Object object, @PathVariable Long id) {
        chatService.setEnterStatus(object, id);
    }

    // 채팅방 퇴장
    @PostMapping("/out/{id}")
    public void setOutStatus(@CurrentUser Object object, @PathVariable Long id) {
        chatService.setOutStatus(object, id);
    }

    // 해당 채팅방의 동행 정보
    @GetMapping("/tour-info/{id}")
    public TourInfo getTourIdFromChat(@PathVariable Long id) {
        return chatService.getTourInfoFromChat(id);
    }

    // 해당 채팅방을 이용하는 유저들의 정보
    @GetMapping("/info/{id}")
    public ChatMemberInfo membersInfo(@CurrentUser Object object, @PathVariable Long id) {
        return chatService.getChatMembersInfo(object, id);
    }

    // 읽음 설정
    @PostMapping("/read/{id}")
    public void setReadStatus(@CurrentUser Object object, @PathVariable Long id) {
        chatService.setReadStatus(object, id);
    }

    // 채팅방 생성
    @PostMapping("/new/{id}")
    public ChatResponse createChat(@PathVariable Long id, @CurrentUser Object object) {
        return chatService.createNewChatRoom(id, object);
    }

    // 내 채팅방 리스트
    @GetMapping("/my-list")
    public List<ChatRoom> getMyChatList(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return chatService.getMyChatRooms(member);
    }

    // 채팅방 삭제
    @PostMapping("/delete/{id}")
    public void deleteChatRoom(@PathVariable Long id, @CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        chatService.deleteMyChatRoom(id, member);
    }

    @PostMapping("/delete/all")
    public  void deleteAllMyChatRooms(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        chatService.deleteAllMyChatRooms(member);
    }
}
