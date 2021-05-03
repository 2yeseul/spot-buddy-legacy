package kr.co.spotbuddy.infra.config.auth;

import kr.co.spotbuddy.infra.domain.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;
    private String email;


    public SessionUser(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
