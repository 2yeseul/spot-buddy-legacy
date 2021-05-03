package kr.co.spotbuddy.modules.violation;

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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;
import kr.co.spotbuddy.modules.violation.dto.MemberViolationDto;
import kr.co.spotbuddy.modules.violation.dto.PostViolationDto;
import kr.co.spotbuddy.modules.violation.dto.TourViolationDto;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
class ViolationControllerTest {

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
    @WithMockUser
    @DisplayName("댓글 신고")
    void test_comment_violate() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"));

        PostViolationDto postViolationDto = PostViolationDto.builder()
                .violationIndex(0)
                .etc("기타 이유")
                .build();

        Long commentId = 4002L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/report-comment-violate/{commentId}", commentId).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postViolationDto)))
                .andDo(print())
                .andDo(document("report-comment-violate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 id")
                        ),
                        requestFields(
                                fieldWithPath("violationIndex").type(JsonFieldType.NUMBER).description("신고 항목 인덱스").optional(),
                                fieldWithPath("etc").type(JsonFieldType.STRING).description("기타").optional()
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("커뮤니티 신고")
    void test_post_violate() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email2@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname2"));

        PostViolationDto postViolationDto = PostViolationDto.builder()
                .violationIndex(0)
                .etc("비속어 ㅡㅡ")
                .build();

        Long postId = 659L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/report-post-violate/{postId}", postId).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postViolationDto)))
                .andDo(print())
                .andDo(document("report-post-violate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("커뮤니티 id")
                        ),
                        requestFields(
                                fieldWithPath("violationIndex").type(JsonFieldType.NUMBER).description("신고 항목 인덱스").optional(),
                                fieldWithPath("etc").type(JsonFieldType.STRING).description("기타").optional()
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("프로필 신고")
    void test_member_violate() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email2@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname2"));

        MemberViolationDto memberViolationDto = MemberViolationDto.builder()
                .etc("나쁜말")
                .nickname("nickname3")
                .build();

        mockMvc.perform(post("/api/report-member-violate").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberViolationDto)))
                .andDo(print())
                .andDo(document("report-member-violate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("신고할 대상의 닉네임"),
                                fieldWithPath("violationIndex").type(JsonFieldType.NUMBER).description("신고 항목 인덱스").optional(),
                                fieldWithPath("etc").type(JsonFieldType.STRING).description("기타").optional()
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("동행 게시글 신고하기")
    void test_violate() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        TourViolationDto tourViolationDto = new TourViolationDto();
        tourViolationDto.setViolationIndex(1);

        Long tourId = 119L;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/report-tour-violate/{tourId}", tourId).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourViolationDto)))
                .andDo(print())
                .andDo(document("report-tour-violate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("tourId").description("신고할 동행 글 id")
                        ),
                        requestFields(
                                fieldWithPath("violationIndex").type(JsonFieldType.NUMBER).description("신고 항목 인덱스").optional(),
                                fieldWithPath("etc").type(JsonFieldType.STRING).description("기타").optional()
                        )
                ));

    }
}