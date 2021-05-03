package kr.co.spotbuddy.modules.passwordFind.dto;

import lombok.Data;

@Data
public class PwResetRequest {
    private String email;
    private String name;
}
