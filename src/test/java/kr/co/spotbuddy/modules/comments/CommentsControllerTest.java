package kr.co.spotbuddy.modules.comments;

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
import kr.co.spotbuddy.modules.comments.dto.CommentForm;
import kr.co.spotbuddy.modules.comments.dto.CommentModify;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class CommentsControllerTest {

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
    @DisplayName("댓글 달기")
    void test_upload_comment() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","2yeseul95@gmail.com")
                .param("password", "12345678ab!"));

        Long postId = 807L;

        CommentForm commentForm = CommentForm.builder()
                .isAnonymous(false)
                .comment("답댓글2")
                .postId(postId)
                .replyStatus(true)
                .replyId(1860L)
                .build();

        mockMvc.perform(post("/api/comment/upload").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentForm)))
                .andDo(print())
                .andDo(document("comment-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestBody(),
                        requestFields(
                                fieldWithPath("anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                                fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("해당 글 id"),
                                fieldWithPath("replyStatus").type(JsonFieldType.BOOLEAN).description("답댓글인지 여부"),
                                fieldWithPath("replyId").type(JsonFieldType.NUMBER).description("답댓글일 경우, 답해진 댓글 Id")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 삭제")
    void test_delete_comment() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        Long id = 785L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/comment/delete/{id}", id).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("comment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("댓글 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 수정 테스트")
    void test_modify_comment() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","fakesafety95@gmail.com")
                .param("password", "12345678ab!"));

        Long id = 4933L;

        CommentModify commentModify = CommentModify.builder()
                .commentId(id)
                .comment("댓글 수정 다시")
                .isAnonymous(false)
                .build();

        mockMvc.perform(post("/api/comment/modify")
                .contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentModify)))
                .andDo(print())
                .andDo(document("comment-modify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestBody(),
                        requestFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 Id"),
                                fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글"),
                                fieldWithPath("anonymous").type(JsonFieldType.BOOLEAN).description("익명여부")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 조회")
    void test_get_comments() throws Exception {
        Long id = 2299L;
        mockMvc.perform(get("/api/comment/list/{id}", id)
                .contextPath("/api"))
                .andDo(print());/*
                .andDo(document("comment-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 글 id")
                        ),
                        responseFields(
                                fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("댓글 id"),
                                fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("댓글 좋아요 수"),
                                fieldWithPath("[].anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                                fieldWithPath("[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("[].commentTime").type(JsonFieldType.STRING).description("댓글 시간"),
                                fieldWithPath("[].replyStatus").type(JsonFieldType.BOOLEAN).description("답댓글 여부"),
                                fieldWithPath("[].replyId").type(JsonFieldType.NUMBER).description("답댓글 ID")
                        )
                ));*/
    }

    @Test
    @DisplayName("내 댓글 리스트")
    @WithMockUser
    void test_get_my_comments() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email21@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/comment/my-list").contextPath("/api"))
                .andDo(print())
                .andDo(document("comment-my-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("커뮤니티 글 id"),
                                fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("댓글 id"),
                                fieldWithPath("[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("[].teamIndex").type(JsonFieldType.NUMBER).description("구단 인덱스"),
                                fieldWithPath("[].postsTime").type(JsonFieldType.STRING).description("글 작성 시간"),
                                fieldWithPath("[].commentsTime").type(JsonFieldType.STRING).description("댓글 작성 시간"),
                                fieldWithPath("[].viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("[].commentCount").type(JsonFieldType.NUMBER).description("댓글수"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("제목")
                        )
                ));
    }
 }