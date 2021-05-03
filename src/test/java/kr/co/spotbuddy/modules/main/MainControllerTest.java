package kr.co.spotbuddy.modules.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("로그인 성공")
    void do_login() throws Exception {
        Member member = new Member();
        member.setEmail("email0@email.com");
        member.setPassword("12345678ab!");
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!")
                .param("rememberMe", "true"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 오류")
    void do_login_fail() throws Exception {
            mockMvc.perform(post("/login")
                    .param("email", "email2@email.com")
                    .param("password", "12345678")
                    .with(csrf())).andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error"))
                    .andExpect(unauthenticated());
    }

    @WithMockUser
    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/api/logout").contextPath("/api"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/"))
                .andExpect(unauthenticated())
                .andDo(document("logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()))
                );
    }

    @Test
    @WithMockUser
    @DisplayName("회원 정보 조회")
    void test_login() throws Exception {
        Member member = new Member();
        member.setEmail("email0@email.com");
        member.setPassword("12345678ab!");
        mockMvc.perform(post("/api/login").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andExpect(authenticated().withUsername("nickname0"))
                .andDo(print());

        mockMvc.perform(post("/api/users").contextPath("/api"))
                .andExpect(authenticated().withUsername("nickname0"))
                .andDo(print())
                .andDo(document("now-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                        ));
    }


}
