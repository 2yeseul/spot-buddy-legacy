package kr.co.spotbuddy.modules.posts;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.modules.member.MemberRepository;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class PostsHideControllerTest {
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

    @Autowired
    MemberRepository memberRepository;

    @Test
    @WithMockUser
    @DisplayName("게시글 숨기기 테스트")
    void test_do_hide_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "skdisk17!"));

        Long id = 6L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/hide/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("hide-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 글 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("해당 게시글이 숨긴 게시글인지 확인")
    void test_is_hide_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "skdisk17!"));

        Long id = 6L;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/hide-status/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("hide-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 글 id")
                        ),
                        responseFields(
                                fieldWithPath("hideStatus").type(JsonFieldType.BOOLEAN).description("true 일 땐 숨긴 글, false 일 땐 X")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("숨기기 취소")
    void test_cancel_hide() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "skdisk17!"));

        Long id = 6L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/cancel-hide/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("hide-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 글 id")
                        )
                ));
    }
}