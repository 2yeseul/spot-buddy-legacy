package kr.co.spotbuddy.modules.member;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.dto.ResendEmail;
import kr.co.spotbuddy.modules.member.dto.SignUpForm;
import kr.co.spotbuddy.modules.member.dto.ValidMember;
import kr.co.spotbuddy.modules.member.dto.YourProfileView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;

import java.util.Random;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @MockBean
    S3UploaderImpl s3Uploader;

    @MockBean
    AmazonS3 amazonS3;

    @MockBean
    AmazonCloudFormation amazonCloudFormation;

/*

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
*/

    @Test
    void test_resend_email_test() throws Exception {
        ResendEmail resendEmail = new ResendEmail();
        resendEmail.setEmail("jaregib554@ddwfzp.com");

        mockMvc.perform(post("/api/resend-email").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendEmail)))
                .andDo(print())
                .andDo(document("resend-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ??????")
    void send_confirm_mail_test() throws Exception {
        Member member = new Member();
        member.setEmail("fakesafety95@gmail.com");
        member.setPassword("12345678ab!");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member)));

        mockMvc.perform(post("/send-email")).andDo(print());
    }



    // api docs - ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ????????? ??????")
    void my_profile_view_test() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/my-profile").contextPath("/api"))
                .andDo(print())
                .andDo(document("my-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("confirmedTourCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("??? ?????????"),
                                fieldWithPath("popularSimpleReviews[]").type(JsonFieldType.ARRAY).description("?????? ?????? ?????? top3"),
                                fieldWithPath("latestDetailReviews[]").type(JsonFieldType.ARRAY).description("?????? ????????? ?????? ??????")

                        ))
                );
    }

    @Test
    @DisplayName("?????? ?????? ????????? ??????")
    void your_profile_view_test() throws Exception {

        String nickname = "nickname3";
        YourProfileView yourProfileView = new YourProfileView();
        yourProfileView.setNickname(nickname);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/your-profile").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yourProfileView)))
                .andDo(document("your-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("confirmedTourCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("??? ?????????")

                        ))
                );
    }
    // api ?????? ??????
    @Test
    @DisplayName("???????????? - ??????")
    void sign_up_process() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("email120@email.com");
        signUpForm.setNickname("nickname120");
        signUpForm.setBirth("20201010");
        signUpForm.setName("??????");
        signUpForm.setPassword("12345678ab!");
        signUpForm.setTeamIndex(2);
        signUpForm.setGender(1);


        mockMvc.perform(post("/api/sign-up").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm)))
                .andDo(print())
                .andDo(document("sign-up",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("gender").type(JsonFieldType.NUMBER).description("?????? - 1 : ???, 2 : ???"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("????????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void send_email_token() throws Exception {

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    void signUpSubmit_with_wrong_input() throws Exception {

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("custom1@gmail.com");
        signUpForm.setNickname("?????????????????????????????????????????????");
        signUpForm.setName("??????");
        signUpForm.setBirth("20210101");
        signUpForm.setPassword("12345678ab!");
        signUpForm.setTeamIndex(new Random().nextInt(9));
        signUpForm.setGender(new Random().nextInt(1) + 1);

        mockMvc.perform(post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm)))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ????????? ???")
    void check_nickname_exists() throws Exception {
        String nickname = "nickname0";
        assertTrue(memberRepository.existsByNickname(nickname));
        mockMvc.perform(post("/valid-nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nickname)))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????????")
    void bad_word_nickname() throws Exception {
        ValidMember validMember = new ValidMember();
        validMember.setNickname("?????????");
        mockMvc.perform(post("/valid-nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validMember)))
                .andDo(print());
    }
    // api doc ??????
    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    void check_nickname_ok() throws Exception {
        String nickname = "??????";
        ValidMember validMember = new ValidMember();
        validMember.setNickname(nickname);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/valid-nickname" ).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validMember)))
                .andDo(print())
                .andDo(document("valid-nickname",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestBody(),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("ok - ?????? X / no - ?????? O / bad - ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    void check_email_ok() throws Exception {
        String email = "spotzz@email.com";
        ValidMember validMember = new ValidMember();
        validMember.setEmail(email);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/valid-email").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validMember)))
                .andDo(print())
                .andDo(document("valid-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestBody(),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("ok - ?????? X / no - ?????? O")
                        )
                ));
    }


/////////// Test DB ?????? ///////////
/*
    @Test
    void insert_test_member_db() throws Exception {
        for(int i=0; i<100; i++) {

            SignUpForm signUpForm = new SignUpForm();
            signUpForm.setEmail("email" + i + "@email.com");
            signUpForm.setNickname("nickname" + i);
            signUpForm.setName("??????");
            signUpForm.setBirth("20210101");
            signUpForm.setPassword("12345678ab!");
            signUpForm.setTeamIndex(new Random().nextInt(9));
            signUpForm.setGender(new Random().nextInt(1) + 1);

            memberService.processNewMember(signUpForm);
        }
    }

*/

    @Test
    void insert_test_member_db() throws Exception {

            SignUpForm signUpForm = new SignUpForm();
            signUpForm.setEmail("sfdsfsdf5@gmail.com");
            signUpForm.setNickname("spotsdf2");
            signUpForm.setName("??????");
            signUpForm.setBirth("20210101");
            signUpForm.setPassword("12345678ab!");
            signUpForm.setTeamIndex(new Random().nextInt(9));
            signUpForm.setGender(new Random().nextInt(1) + 1);

        mockMvc.perform(post("/api/sign-up").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm))).andDo(print());

    }

    @Test
    void test_delete_member() throws Exception {
        Member member = memberRepository.findByEmail("testemail@email.com");
        memberRepository.delete(member);
    }

}