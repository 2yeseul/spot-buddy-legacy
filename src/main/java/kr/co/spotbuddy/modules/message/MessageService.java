package kr.co.spotbuddy.modules.message;

import kr.co.spotbuddy.infra.domain.*;
import kr.co.spotbuddy.infra.firebase.FirebaseCloudMessageService;
import kr.co.spotbuddy.modules.block.BlockRepository;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.message.chat.ChatRepository;
import kr.co.spotbuddy.modules.message.dto.MessageForm;
import kr.co.spotbuddy.modules.message.dto.MessagesView;
import kr.co.spotbuddy.modules.pushAlarmTF.PushAlarmTFRepository;
import kr.co.spotbuddy.modules.token.TokenInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate template;
    private final TokenInfoRepository tokenInfoRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService; // push alarm
    private final PushAlarmTFRepository pushAlarmTFRepository;
    private final BlockRepository blockRepository;

    @Transactional
    public void setMessageStatus(Long id) {
        Chat chat = chatRepository.findById(id).get();

        for(int i=0;i<chat.getMessages().size();i++) {
            Message message = chat.getMessages().get(i);
            message.readStatusTrue(true);
            messageRepository.save(message);
        }
    }
    

    // 메시지 전송 to subscribers
    @Transactional
    public void sendChatMessage(MessageForm chatMessage) throws Exception {
        // 발신자
        Member sender = memberRepository.findByNickname(chatMessage.getSender());
        Chat chat = chatRepository.findById(chatMessage.getChatRoomId()).get();

        // 수신자
        Member receiver = (sender.equals(chat.getRequestMember())) ? chat.getTourUploader() : chat.getRequestMember();

        saveMessage(chatMessage, sender, chat);
        setReadStatus(sender, chat);

        // sub
        sendMessage(chatMessage);

        // app 사용자일 경우 push alarm
        if(tokenInfoRepository.existsByMember(receiver)) pushAlarmToReceiver(receiver, chatMessage);

        resetDeleteState(chat);
    }

    private void resetDeleteState(Chat chat) {
        chat.resetDeleteState();
        chatRepository.save(chat);
    }

    // app push alarm
    private void pushAlarmToReceiver(Member receiver, MessageForm messageForm) throws Exception {
        boolean messageAlarmOn = false;
        if(pushAlarmTFRepository.existsByMember(receiver)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(receiver);
            messageAlarmOn = pushAlarmTF.isMessageTurnOn();
        }

        // 푸시알람 설정이 없을 땐, 기본이 true 이기 때문에 true 설정
        if(!pushAlarmTFRepository.existsByMember(receiver)) messageAlarmOn = true;

        // 환경설정 -> 푸시알람 on 한 사람에게만 알람 보내기
        if(messageAlarmOn) {
            String token = tokenInfoRepository.findByMember(receiver).getToken();
            String nickname = "[" + messageForm.getSender() + "]";
            Tour tour = chatRepository.findById(messageForm.getChatRoomId()).get().getTour();
            String title = nickname + " " + tour.getTourTitle();
            String body = messageForm.getMessage();
            Chat chat = chatRepository.findById(messageForm.getChatRoomId()).get();
            String path = "http://3.35.213.43/chat-detail/" + chat.getId();

            boolean isReceiverEnter = (receiver.equals(chat.getRequestMember())) ? chat.isRequestMemberEnter() : chat.isTourUploaderEnter();

            Member sender = memberRepository.findByNickname(messageForm.getSender());

            if(!isReceiverEnter && !blockRepository.isAlreadyBlocked(receiver, sender))
                firebaseCloudMessageService.sendMessageTo(token, title, body, path);
        }
    }


    private void setReadStatus(Member sender, Chat chat) {
        if(sender.equals(chat.getRequestMember())) {
            chat.requestMemberState(true);
            chat.tourUploaderState(false);
        }

        else {
            chat.requestMemberState(false);
            chat.tourUploaderState(true);
        }
    }


    // 채팅방 별 메시지 전체 보기
    public List<MessagesView> getMessagesViews(Long id) {
        Chat chat = chatRepository.findById(id).get();
        List<Message> messages = messageRepository.findAllByChatOrderByIdDesc(chat);
        List<MessagesView> messagesViews = new ArrayList<>();
        makeMessagesViewList(messages, messagesViews);


        return messagesViews;
    }

    private void makeMessagesViewList(List<Message> messages, List<MessagesView> messagesViews) {
        for (Message message : messages) {
            String sender = "";
            if(message.getSender().getNickname()!=null) sender = message.getSender().getNickname();

            MessagesView messagesView = MessagesView.builder()
                    .message(message.getMessage())
                    .sender(sender)
                    .messageTime(message.getMessageTime())
                    .readStatus(message.isReadStatus())
                    .build();

            messagesViews.add(messagesView);
        }
    }

    private void sendMessage(MessageForm chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        template.convertAndSend("/api/sub/" + chatRoomId, chatMessage);
    }


    private void saveMessage(MessageForm chatMessage, Member sender, Chat chat) {
        Message message = Message.builder()
                .message(chatMessage.getMessage())
                .messageTime(chatMessage.getMessageTime())
                .sender(sender)
                .chat(chat)
                .build();

        messageRepository.save(message);
    }

}
