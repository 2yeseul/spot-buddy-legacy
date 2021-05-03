package kr.co.spotbuddy.modules.passwordFind.dto;

import lombok.Data;

@Data
public class PasswordToken {
    private String email;
    private int token;
}
