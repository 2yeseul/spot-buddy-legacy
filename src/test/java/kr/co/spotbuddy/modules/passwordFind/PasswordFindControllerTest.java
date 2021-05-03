package kr.co.spotbuddy.modules.passwordFind;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.passwordFind.dto.PasswordReset;
import kr.co.spotbuddy.modules.passwordFind.dto.PasswordToken;
import kr.co.spotbuddy.modules.passwordFind.dto.PwResetRequest;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class PasswordFindControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    S3UploaderImpl s3Uploader;

    @MockBean
    AmazonS3 amazonS3;

    @MockBean
    AmazonCloudFormation amazonCloudFormation;

    @Test
    @DisplayName("이메일 전송")
    void test_send_pw_email() throws Exception {
        PwResetRequest pwResetRequest = new PwResetRequest();
        pwResetRequest.setEmail("jessica6851@naver.com");
        pwResetRequest.setName("이름");
        mockMvc.perform(post("/setting/password/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pwResetRequest)))
                .andDo(print())
                .andDo(document("password-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("message")
                        )
                ));
    }


    @Test
    @DisplayName("토큰 체크")
    void test_check_token() throws Exception {
        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setEmail("20152917@sungshin.ac.kr");
        passwordToken.setToken(76813);

        mockMvc.perform(post("/setting/password/token-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordToken)))
                .andDo(print())
                .andDo(document("password-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                                fieldWithPath("token").type(JsonFieldType.NUMBER).description("토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("correct - 인증번호 맞음, incorrect - 인증번호 틀림")
                        )
                ));
    }


    @Test
    @DisplayName("패스워드 재설정")
    void test_reset_password() throws Exception {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail("20152917@sungshin.ac.kr");
        passwordReset.setPassword("12345678ab!");

        mockMvc.perform(post("/setting/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordReset)))
                .andDo(print())
                .andDo(document("password-reset",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("password")
                        )
                ));
    }

}