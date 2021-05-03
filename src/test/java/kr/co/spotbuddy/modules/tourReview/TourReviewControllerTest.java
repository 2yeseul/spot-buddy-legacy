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
        homeSearch.setAwayTeam("KIA"); homeSearch.setHomeTeam("키움"); homeSearch.setGameDate(LocalDate.of(2021, 4, 8));
        homeSearch.setLocation("고척");

        mockMvc.perform(post("/api/home/tour-search").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(homeSearch)))
                .andDo(print())
                .andDo(document("home-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("awayTeam").type(JsonFieldType.STRING).description("원정팀(team1)"),
                                fieldWithPath("homeTeam").type(JsonFieldType.STRING).description("홈팀(team2)"),
                                fieldWithPath("gameDate").type(JsonFieldType.STRING).description("일정(yyyy-mm-dd)"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("장소")
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
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("상대방 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("todayReview").type(JsonFieldType.BOOLEAN).description("오늘 평가를 했는지 여부")
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
                                fieldWithPath("[].getReviewed").type(JsonFieldType.STRING).description("평가를 받은 사람(본인)"),
                                fieldWithPath("[].sendReview").type(JsonFieldType.STRING).description("평가한 사람"),
                                fieldWithPath("[].reviewDate").type(JsonFieldType.STRING).description("평가한 날"),
                                fieldWithPath("[].simpleTourReviews[].id").type(JsonFieldType.NUMBER).description("간단 동행 평가 db id(무시)"),
                                fieldWithPath("[].simpleTourReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("간단 동행 항목 index"),
                                fieldWithPath("[].detailReview").type(JsonFieldType.STRING).description("상세 동행 평가").optional(),
                                fieldWithPath("[].anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                                fieldWithPath("[].weatherIndex").type(JsonFieldType.NUMBER).description("날씨 평가 인덱스")
                        )
                ));


    }

    @Test
    @WithMockUser
    @DisplayName("나의 프로필 보기")
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
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("날씨 지수"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("날씨 지수"),
                                fieldWithPath("confirmedTourCount").type(JsonFieldType.NUMBER).description("동행 확정 홧수"),
                                fieldWithPath("popularReviews[]").type(JsonFieldType.ARRAY).description("많이 받은 리뷰 top3"),
                                fieldWithPath("popularReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("간단 리뷰 인덱스"),
                                fieldWithPath("popularReviews[].ratio").type(JsonFieldType.NUMBER).description("리뷰 받은 비율"),
                                fieldWithPath("latestReviews[]").type(JsonFieldType.ARRAY).description("최근 리뷰"),
                                fieldWithPath("latestReviews[].nickname").type(JsonFieldType.STRING).description("리뷰 작성자 닉네임"),
                                fieldWithPath("latestReviews[].review").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("latestReviews[].anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부").optional(),
                                fieldWithPath("latestReviews[].reviewDate").type(JsonFieldType.STRING).description("리뷰 달은 날짜 - 수정 예정")

                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("상대 프로필 보기")
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
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("birth").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("날씨 지수"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("날씨 지수"),
                                fieldWithPath("confirmedTourCount").type(JsonFieldType.NUMBER).description("동행 확정 홧수"),
                                fieldWithPath("popularReviews[]").type(JsonFieldType.ARRAY).description("많이 받은 리뷰 top3"),
                                fieldWithPath("popularReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("간단 리뷰 인덱스"),
                                fieldWithPath("popularReviews[].ratio").type(JsonFieldType.NUMBER).description("리뷰 받은 비율"),
                                fieldWithPath("latestReviews[]").type(JsonFieldType.ARRAY).description("최근 리뷰"),
                                fieldWithPath("latestReviews[].nickname").type(JsonFieldType.STRING).description("리뷰 작성자 닉네임"),
                                fieldWithPath("latestReviews[].review").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("latestReviews[].anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부").optional(),
                                fieldWithPath("latestReviews[].reviewDate").type(JsonFieldType.STRING).description("리뷰 달은 날짜 - 수정 예정")

                        )
                ));
    }


    // api doc 작성 완료
    @WithMockUser
    @Test
    @DisplayName("동행 평가")
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
                .detailReview("사진 굿굿")
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
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("평가를 받는 사람의 닉네임"),
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("간단 평가(배열)"),
                                fieldWithPath("detailReview").type(JsonFieldType.STRING).description("상세 평가"),
                                fieldWithPath("weatherIndex").type(JsonFieldType.NUMBER).description("5, 3, 0 ,-3, -5 (하늘이, 맑음이, 구름이, 흐림이, 천둥이)  "),
                                fieldWithPath("anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부")
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("내가 받은 동행 평가 보기")
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
                                fieldWithPath("[].getReviewed").type(JsonFieldType.STRING).description("평가를 받은 사람(본인)"),
                                fieldWithPath("[].sendReview").type(JsonFieldType.STRING).description("평가한 사람"),
                                fieldWithPath("[].simpleTourReviews[].id").type(JsonFieldType.NUMBER).description("간단 동행 평가 db id(무시)"),
                                fieldWithPath("[].simpleTourReviews[].reviewIndex").type(JsonFieldType.NUMBER).description("간단 동행 항목 index"),
                                fieldWithPath("[].detailReview").type(JsonFieldType.STRING).description("상세 동행 평가"),
                                fieldWithPath("[].anonymous").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                                fieldWithPath("[].weatherIndex").type(JsonFieldType.NUMBER).description("날씨 평가 인덱스")
                        )
                ));
    }
}