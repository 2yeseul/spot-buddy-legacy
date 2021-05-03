package kr.co.spotbuddy.modules.message;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.message.chat.ChatService;
import kr.co.spotbuddy.modules.message.dto.MessageForm;
import kr.co.spotbuddy.modules.message.dto.MessagesView;
import kr.co.spotbuddy.modules.response.ReadStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final ChatService chatService;
    private final MemberType memberType;

    // 메시지 pub
    @MessageMapping("/message")
    public void sendMessage(@Payload @RequestBody MessageForm chatMessage) throws Exception {
        messageService.sendChatMessage(chatMessage);
    }

    @GetMapping("/message-list/{id}")
    public List<MessagesView> getMessages(@PathVariable Long id, @CurrentUser Object object){
        chatService.setReadStatus(object, id);
        return messageService.getMessagesViews(id);
    }

    @PostMapping("/read/message/{id}")
    public ReadStatus setReadTrue(@PathVariable Long id) {
        messageService.setMessageStatus(id);
        return ReadStatus.builder().readStatus(true).build();
    }

}


