package kr.co.spotbuddy.modules.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.confirmedTour.ConfirmedTourRepository;
import kr.co.spotbuddy.infra.firebase.FirebaseCloudMessageService;
import kr.co.spotbuddy.modules.token.TokenInfoRepository;
import kr.co.spotbuddy.modules.tour.TourRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberType memberType;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ConfirmedTourRepository confirmedTourRepository;

    @Autowired
    TokenInfoRepository tokenInfoRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FirebaseCloudMessageService firebaseCloudMessageService;

    @Test
    @WithMockUser
    void test_set_read() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"));

        Long id = 1185L;

        mockMvc.perform(post("/read/message/{id}", id))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void test_push_alarm() throws Exception {
        Member member = memberRepository.findById(1089L).get();
        String token = tokenInfoRepository.findByMember(member).getToken();
        String nickname = "[test]";
        String title = "test title";
        String body = "test body";
        String path = "http://3.35.213.43/";

        firebaseCloudMessageService.sendMessageTo(token, title, body, path);
    }
}