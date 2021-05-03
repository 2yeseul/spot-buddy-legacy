package kr.co.spotbuddy.modules.block;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.modules.block.dto.BlockRequest;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;
import kr.co.spotbuddy.modules.tour.TourRepository;
import kr.co.spotbuddy.modules.tour.TourService;
import kr.co.spotbuddy.modules.tourReview.TourReviewRepository;
import kr.co.spotbuddy.modules.tourTheme.TourThemeRepository;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class BlockControllerTest {

    @Autowired
    TourRepository tourRepository;

    @Autowired
    TourThemeRepository tourThemeRepository;

    @Autowired
    TourReviewRepository tourReviewRepository;

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
    TourService tourService;

    @Test
    @WithMockUser
    void test_do_block_member() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setNickname("nickname12");

        mockMvc.perform(post("/api/block/member").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blockRequest)))
                .andDo(print())
                .andDo(document("block-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("차단 해제할 사람의 닉네임")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("차단 취소")
    void test_cancel_my_block() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setNickname("김민수1");

        mockMvc.perform(post("/api/block/cancel").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blockRequest)))
                .andDo(print())
                .andDo(document("block-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("차단 해제할 사람의 닉네임")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("내 차단 리스트")
    void test_get_my_blocks() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/block/my-list").contextPath("/api"))
                .andDo(print())
                .andDo(document("block-my-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("차단 당한 사람 닉네임")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("차단하기 - 동행 상세 페이지")
    void test_block_tour() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email23@email.com")
                .param("password", "12345678ab!"));

        Long id = 1502L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/block/tour/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("block-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("동행 글 id")
                        )
                ));


    }

    @Test
    @WithMockUser
    @DisplayName("차단filter")
    void test_() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email23@email.com")
                .param("password", "12345678ab!"));


        mockMvc.perform(get("/filter")).andDo(print());

    }

    @Test
    @WithMockUser
    void test_get_filtered_tours() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email23@email.com")
                .param("password", "12345678ab!"));

        Integer page = 0;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tour-list/filter/{page}", page).contextPath("/api"))
                .andDo(print())
                .andDo(document("list-filtered-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("page").description("페이지 넘버 (0부터 시작)")
                        ),
                        responseBody()
                ));

    }

    @Test
    @WithMockUser
    void test_get_filtered_popular() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email23@email.com")
                .param("password", "12345678ab!"));

        int page = 0;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tour-list/filter/popular/{page}", page).contextPath("/api"))
                .andDo(print())
                .andDo(document("list-filtered-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("page").description("페이지 넘버 (0부터 시작)")
                        ),
                        responseBody()
                ));
    }

    @Test
    @WithMockUser
    void test_delete_tour() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"));

        tourRepository.deleteById(1768L);
    }

}