package kr.co.spotbuddy.modules.posts;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.job.CommunityScheduler;
import kr.co.spotbuddy.modules.posts.dto.BadWordsRequest;
import kr.co.spotbuddy.modules.posts.dto.PostSearch;
import kr.co.spotbuddy.modules.posts.dto.TeamRequest;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class PostsControllerTest {

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
    PostsRepository postsRepository;

    @MockBean
    CommunityScheduler communityScheduler;

    @Test
    void test_reset_today_view() throws Exception {
        postsRepository.resetTodayView();
    }

    @Test
    @WithMockUser
    @DisplayName("????????? ????????? ?????? ??????")
    void test_is_bad_words_in() throws Exception {
        String title = "??????";
        String content = "gsidfsdf son of bitch ????????????????????????????????????";

        BadWordsRequest badWordsRequest = new BadWordsRequest();

        badWordsRequest.setTitle(title);
        badWordsRequest.setContent(content);

        mockMvc.perform(get("/api/community/content-check").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badWordsRequest)))
                .andDo(print())
                .andDo(document("content-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??? ??????")
                        ),
                        responseFields(
                                fieldWithPath("badWordsIn").type(JsonFieldType.BOOLEAN).description("true??? ????????? ??????")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("???????????? ?????????")
    void test_get_popular_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email80@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/community/popular/0").contextPath("/api"))
                .andDo(print())
                .andDo(document("community-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("???????????? ??? id"),
                                fieldWithPath("[].postsDate").type(JsonFieldType.STRING).description("??? ?????????"),
                                fieldWithPath("[].viewCount").type(JsonFieldType.NUMBER).description("?????? ???"),
                                fieldWithPath("[].commentCount").type(JsonFieldType.NUMBER).description("?????? ???"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("[].teamIndex").type(JsonFieldType.NUMBER).description("?????? index"),
                                fieldWithPath("[].photoUrls").type(JsonFieldType.STRING).description("?????? ??????(??? ??????)").optional()
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("???????????? ??? ??????")
    void test_upload_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email81@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(post("/api/community/upload").contextPath("/api")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", "title")
                .param("content", "content")
                .param("isAnonymous", "false")
                .param("category", "1")
                .param("teamIndex", "2"))
                .andDo(document("community-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("title").description("??? ??????"),
                                parameterWithName("content").description("??? ??????"),
                                parameterWithName("isAnonymous").description("?????? ??????"),
                                parameterWithName("multipartFile").description("??????(??????)???").optional(),
                                parameterWithName("category").description("??? ????????????"),
                                parameterWithName("teamIndex").description("?????? ?????????")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("???????????? ??? ??????")
    void test_view_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        Long id = 807L;
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/detail/{id}", id).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("community-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("id").description("???????????? ??? id")),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("???????????? ??? id"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("??? ????????? ?????????"),
                                fieldWithPath("anonymous").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("postsTime").type(JsonFieldType.STRING).description("??? ?????? ??????"),
                                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("?????????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??? ??????"),
                                fieldWithPath("commentsCount").type(JsonFieldType.NUMBER).description("?????? ???"),
                                fieldWithPath("photoUrls[]").type(JsonFieldType.ARRAY).description("?????? ??????")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("delete")
    void test_delete_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        Long id = 657L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/delete/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("community-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("id").description("???????????? ??? id"))
                ));

    }

    @Test
    @DisplayName("?????? ????????? ??? ??? ??????")
    void test_get_team_posts() throws Exception {
        Integer page = 0;

        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setCategory(-1); teamRequest.setTeamIndex(2);
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/team/{page}", page).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamRequest)))
                .andDo(print())
                .andDo(document("community-team",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("page").description("page ??????")),
                        requestFields(
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("category").type(JsonFieldType.NUMBER).description("???????????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ???")
    void test_count_members() throws Exception {
        int teamIndex = 1;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/team/count/{teamIndex}", teamIndex).contextPath("/api"))
                .andDo(print())
                .andDo(document("community-members",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamIndex").description("?????? ?????????")),
                        responseFields(
                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER).description("?????? ???")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ??????")
    void test_search_posts_home() throws Exception {
        PostSearch postSearch = new PostSearch();
        postSearch.setCategory(-1); postSearch.setTeamIndex(-1); postSearch.setKeyword("??????");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/search").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postSearch)))
                .andDo(print())
                .andDo(document("community-search-home",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("keyword").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("category").type(JsonFieldType.NUMBER).description("???????????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ??????????????? ??????")
    void test_search_posts_team() throws Exception {
        PostSearch postSearch = new PostSearch();
        postSearch.setCategory(-1); postSearch.setTeamIndex(2); postSearch.setKeyword("??????");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/search").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postSearch)))
                .andDo(print())
                .andDo(document("community-search-team",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("keyword").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("category").type(JsonFieldType.NUMBER).description("???????????? ?????????")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("?????? ??? ???")
    void test_my_post_list() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email1@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/post/my-list").contextPath("/api"))
                .andDo(print())
                .andDo(document("community-my-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ??? ??????")
    void test_get_member_count() throws Exception {
        int teamIndex = 0;
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/team/count/{teamIndex}", teamIndex).contextPath("/api"))
                .andDo(print())
                .andDo(document("community-member-number",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                          parameterWithName("teamIndex").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER).description("?????? ???")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("??? ??????")
    void test_modify_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","jessica6851@naver.com")
                .param("password", "skdisk17!"));

        Long id = 2765L;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/modify/{id}", id).contextPath("/api")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", "0413 ?????? ??????")
                .param("content", "0413 ?????? ??????")
                .param("isAnonymous", "false")
                .param("category", "1"))
                .andDo(document("community-modify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                          parameterWithName("id").description("???????????? ??? id")
                        ),
                        requestParameters(
                                parameterWithName("title").description("??? ??????"),
                                parameterWithName("content").description("??? ??????"),
                                parameterWithName("isAnonymous").description("?????? ??????"),
                                parameterWithName("deleteFileNames").description("???????????? ?????? ???????????? ?????????(???????????? ????????? ?????? ??????)").optional(),
                                parameterWithName("multipartFile").description("??????(??????)???").optional(),
                                parameterWithName("category").description("??? ????????????")
                        )
                ));
    }
}