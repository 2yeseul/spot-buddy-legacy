package kr.co.spotbuddy.modules.message.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ChatMemberInfo {
    private String myNickname;
    private String yourNickname;
}
