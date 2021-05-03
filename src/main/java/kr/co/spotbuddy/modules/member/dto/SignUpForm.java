package kr.co.spotbuddy.modules.member.dto;

import lombok.Data;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SignUpForm {

    @NotBlank
    @Email
    private String email;

    // 영문, 한글, 숫자
    @NotBlank
    @Length(min = 2, max = 12)
    private String nickname;

    @NotBlank
    private String name;

    @NotNull
    private String birth;

    // 영문, 특수문자, 숫자
    @NotBlank
    @Length(min = 8, max = 20)
    private String password;

    @NotNull
    private int teamIndex;

    @NotNull
    private int gender;

    private String isAgreeOnGetPromotion;

}
