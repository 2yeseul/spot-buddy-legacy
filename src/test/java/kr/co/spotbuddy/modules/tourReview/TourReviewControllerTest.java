package kr.co.spotbuddy.modules.tourReview;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;
import kr.co.spotbuddy.modules.tour.dto.HomeSearch;
import kr.co.spotbuddy.modules.tourReview.dto.TourReviewDetail;
import kr.co.spotbuddy.modules.tourReview.dto.TourReviewDto;
import kr.co.spotbuddy.modules.tourReview.dto.YourNickname;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class TourReviewControllerTest {

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
    void test_home_search() throws Exception {
        HomeSearch homeSearch = new HomeSearch();
        homeSearch.setAwayTeam("KIA"); homeSearch.setHomeTeam("??????"); homeSearch.setGameDate(LocalDate.of(2021, 4, 8));
        homeSearch.setLocation("??????");

        mockMvc.perform(post("/api/home/tour-search").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(homeSearch)))
                .andDo(print())
                .andDo(document("home-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("awayTeam").type(JsonFieldType.STRING).description("?????????(team1)"),
                                fieldWithPath("homeTeam").type(JsonFieldType.STRING).description("??????(team2)"),
                                fieldWithPath("gameDate").type(JsonFieldType.STRING).description("??????(yyyy-mm-dd)"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("??????")
                        )
                ));

    }


    @Test
    @WithMockUser
    void test_is_review_possible() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","jessica6851@naver.com")
                .param("password", "12345678ab!"));

        YourNickname yourNickname = new YourNickname();
        yourNickname.setNickname("nickname23");


        mockMvc.perform(post("/api/review-possible").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yourNickname)))
                .andDo(print())
                .andDo(document("tour-review-ok",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("todayReview").type(JsonFieldType.BOOLEAN).description("?????? ????????? ????????? ??????")
                        )
                ));

    }

    @Test
    @WithMockUser
    void test_your_profile_detail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email17@email.com")
                .param("password", "12345678ab!"));

        TourReviewDetail detail = new TourReviewDetail();
        detail.setNickname("nickname0");

        mockMvc.perform(post("/api/tour-review/detail").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detail)))
                .andDo(print())
                .andDo(document("tour-reviews-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].getReviewed").type(JsonFieldType.STRING).description("????????? ?????? ??????(??????)"),
                                fieldWithPath("[].sendReview").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].reviewDate").type(JsonFieldType.STRING).description("????????? ???"),
                                fieldWithPath("[].simpleTourReviews[].id").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? db id(??????)"),
                                fieldWithPath("[].simpleTourReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? index"),
                                fieldWithPath("[].detailReview").type(JsonFieldType.STRING).description("?????? ?????? ??????").optional(),
                                fieldWithPath("[].anonymous").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("[].weatherIndex").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????")
                        )
                ));


    }

    @Test
    @WithMockUser
    @DisplayName("?????? ????????? ??????")
    void test_my_profile() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/my-profile").contextPath("/api"))
                .andDo(print())
                .andDo(document("my-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("confirmedTourCount").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("popularReviews[]").type(JsonFieldType.ARRAY).description("?????? ?????? ?????? top3"),
                                fieldWithPath("popularReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                                fieldWithPath("popularReviews[].ratio").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("latestReviews[]").type(JsonFieldType.ARRAY).description("?????? ??????"),
                                fieldWithPath("latestReviews[].nickname").type(JsonFieldType.STRING).description("?????? ????????? ?????????"),
                                fieldWithPath("latestReviews[].review").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("latestReviews[].anonymous").type(JsonFieldType.BOOLEAN).description("?????? ??????").optional(),
                                fieldWithPath("latestReviews[].reviewDate").type(JsonFieldType.STRING).description("?????? ?????? ?????? - ?????? ??????")

                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("?????? ????????? ??????")
    void test_your_profile() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        YourProfileView yourProfileView = new YourProfileView();
        yourProfileView.setNickname("nickname1");

        mockMvc.perform(post("/api/your-profile").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yourProfileView)))
                .andDo(print())
                .andDo(document("your-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("confirmedTourCount").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("popularReviews[]").type(JsonFieldType.ARRAY).description("?????? ?????? ?????? top3"),
                                fieldWithPath("popularReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                                fieldWithPath("popularReviews[].ratio").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("latestReviews[]").type(JsonFieldType.ARRAY).description("?????? ??????"),
                                fieldWithPath("latestReviews[].nickname").type(JsonFieldType.STRING).description("?????? ????????? ?????????"),
                                fieldWithPath("latestReviews[].review").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("latestReviews[].anonymous").type(JsonFieldType.BOOLEAN).description("?????? ??????").optional(),
                                fieldWithPath("latestReviews[].reviewDate").type(JsonFieldType.STRING).description("?????? ?????? ?????? - ?????? ??????")

                        )
                ));
    }


    // api doc ?????? ??????
    @WithMockUser
    @Test
    @DisplayName("?????? ??????")
    void test_upload_review() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email48@email.com")
                .param("password", "12345678ab!"));

        Set<Integer> reviews = new HashSet<>();

        reviews.add(1);
        reviews.add(4);

        TourReviewDto tourReviewDto = TourReviewDto.builder()
                .nickname("nickname0")
                .reviews(reviews)
                .detailReview("?????? ??????")
                .isAnonymous(true)
                .weatherIndex(5)
                .build();

        mockMvc.perform(post("/api/tour-review").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourReviewDto)))
                .andDo(print())
                .andDo(document("tour-review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????? ????????? ?????????"),
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("?????? ??????(??????)"),
                                fieldWithPath("detailReview").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("weatherIndex").type(JsonFieldType.NUMBER).description("5, 3, 0 ,-3, -5 (?????????, ?????????, ?????????, ?????????, ?????????)  "),
                                fieldWithPath("anonymous").type(JsonFieldType.BOOLEAN).description("?????? ??????")
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ?????? ?????? ??????")
    void my_tour_reviews_test() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email0@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/my-tour-review").contextPath("/api"))
                .andDo(print())
                .andDo(document("my-tour-reviews",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].getReviewed").type(JsonFieldType.STRING).description("????????? ?????? ??????(??????)"),
                                fieldWithPath("[].sendReview").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("[].simpleTourReviews[].id").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? db id(??????)"),
                                fieldWithPath("[].simpleTourReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? index"),
                                fieldWithPath("[].detailReview").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("[].anonymous").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("[].weatherIndex").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????")
                        )
                ));
    }
}