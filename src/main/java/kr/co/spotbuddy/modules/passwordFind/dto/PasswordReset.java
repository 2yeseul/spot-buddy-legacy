package kr.co.spotbuddy.modules.passwordFind.dto;

import lombok.Data;

@Data
public class PasswordReset {
    private String email;
    private String password;
}
