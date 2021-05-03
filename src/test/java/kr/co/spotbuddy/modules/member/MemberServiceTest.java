package kr.co.spotbuddy.modules.member;

import kr.co.spotbuddy.modules.member.dto.SignUpAuthForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() throws Exception {
        SignUpAuthForm signUpAuthForm = new SignUpAuthForm();
        signUpAuthForm.setEmail("jessica6851@naver.com");
        signUpAuthForm.setNickname("spot닉넴");
        signUpAuthForm.setName("이예슬");
        signUpAuthForm.setBirth("19951229");
        signUpAuthForm.setTeamIndex(1);
        signUpAuthForm.setGender(2);

        memberService.saveOauthMember(signUpAuthForm);

        SimpleGrantedAuthority simpleGrantedAuthority;
    }

    @Test
    @WithMockUser
    void test_simple_reviews() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/my-simple-reviews")).andDo(print());
    }
}