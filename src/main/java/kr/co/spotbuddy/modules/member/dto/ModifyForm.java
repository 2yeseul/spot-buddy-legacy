package kr.co.spotbuddy.modules.member.dto;

import lombok.Data;

@Data
public class ModifyForm {
    private String nickname;
    private String password;
    private String birth;
    private int gender;
    private int teamIndex;
}
