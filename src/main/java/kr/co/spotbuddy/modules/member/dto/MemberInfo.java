package kr.co.spotbuddy.modules.member.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MemberInfo {

    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String birth;
    private int gender;

    private int teamIndex;

    private int weather;

    private boolean emailVerified;

}
