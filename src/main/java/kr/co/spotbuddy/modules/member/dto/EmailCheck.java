package kr.co.spotbuddy.modules.member.dto;

import lombok.Data;

@Data
public class EmailCheck {
    private String token;
    private String email;
}
