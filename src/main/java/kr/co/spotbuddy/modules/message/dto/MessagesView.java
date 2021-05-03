package kr.co.spotbuddy.modules.message.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MessagesView {
    private String message;
    private String sender;

    private String messageTime;

    private boolean readStatus;
}
