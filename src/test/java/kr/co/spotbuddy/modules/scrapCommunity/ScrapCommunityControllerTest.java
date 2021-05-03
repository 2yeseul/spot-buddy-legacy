package kr.co.spotbuddy.modules.scrapCommunity;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class ScrapCommunityControllerTest {

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
    void test_is_scraped() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email16@email.com")
                .param("password", "12345678ab!"));

        Long id = 2765L;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/scrap-state/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("scrap-community-state",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 글 id")
                        ),
                        responseFields(
                                fieldWithPath("scrapState").type(JsonFieldType.BOOLEAN).description("스크랩 상태")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_delete_scrap_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email16@email.com")
                .param("password", "12345678ab!"));

        Long id = 2765L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/scrap/delete/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("scrap-community-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 글 id")
                        )
                ));
    }


    @Test
    @WithMockUser
    @DisplayName("스크랩 테스트")
    void test_do_scrap_post() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email16@email.com")
                .param("password", "12345678ab!"));

        Long id = 2765L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/community/scrap/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("scrap-community",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("커뮤니티 글 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("내 스크랩 리스트 보기")
    void test_my_post_scrap() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email1@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/scrap/my-list").contextPath("/api"))
                .andDo(print())
                .andDo(document("scrap-community-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
 }