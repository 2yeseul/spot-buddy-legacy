package kr.co.spotbuddy.modules.message.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MessageForm {
    private String message;
    private Long chatRoomId;
    private String sender;
    private String messageTime;
}
