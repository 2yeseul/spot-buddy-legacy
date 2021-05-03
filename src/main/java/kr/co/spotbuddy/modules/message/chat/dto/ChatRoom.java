package kr.co.spotbuddy.modules.message.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ChatRoom {
    private String tourTitle;
    private String lastMessage;
    private String lastMessageTime;
    private Long chatRoomId;
    private Long tourId;
    private Long lastMessageId;
    private String nickname;

    private boolean readState;
    private boolean yourReadState;
}
