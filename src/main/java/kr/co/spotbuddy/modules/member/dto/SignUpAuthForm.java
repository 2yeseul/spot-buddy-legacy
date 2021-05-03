package kr.co.spotbuddy.modules.member.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SignUpAuthForm {

    @NotBlank
    @Email
    private String email;

    // 영문, 한글, 숫자
    @NotBlank
    @Length(min = 2, max = 12)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9A-Z]{2,12}$")
    private String nickname;

    @NotBlank
    private String name;

    @NotNull
    private String birth;

    @NotNull
    private int teamIndex;

    @NotNull
    private int gender;
}
