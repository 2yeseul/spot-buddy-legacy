package kr.co.spotbuddy.modules.tour;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.modules.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class TourControllerTest {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TourService tourService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
/*
    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
*/


    @Test
    void test_search_tour() throws Exception {

    }

    /*@Test
    @DisplayName("동행 목록 전체 반환 - 인기순 (page)")
    void tour_popular_list_page() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tour-list/popular/{page}", 0).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("tour-list-page-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("page").description("page 넘버(0부터 시작)")
                        )
                ));
    }

    // api doc 작성 완료
    @Test
    @DisplayName("동행 목록 전체 반환 - 최신순 (page)")
    void tour_list_page() throws Exception {
        mockMvc.perform(get("/api/tour-list/{page}", 0).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("tour-list-page",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("page").description("page 넘버(0부터 시작)")
                        ),
                        responseBody())
                );
    }

    // api doc 작성 완료
    @Test
    @WithMockUser
    void close_tour_success() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email2@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname2"));

        Long id = 102L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/close-tour/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("close-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("동행 글 id")
                        ))
                );

    }

    // api doc 작성 완료
    @Test
    @WithMockUser
    @DisplayName("동행 글 삭제 - 성공")
    void delete_tour_success() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email5@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname5"));

        Long id = 140L;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/delete-tour/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("delete-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("동행 글 id")
                        ))
                );
    }

    @Test
    @WithMockUser
    @DisplayName("동행 삭제 - 실패 - 다른 사람 시도 ")
    void delete_tour_fail() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email4@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname4"));

        Long id = 140L;
        mockMvc.perform(post("/delete-tour/{id}", id))
                .andDo(print());
    }

    // api doc 작성 완료
    @Test
    @WithMockUser
    @DisplayName("동행 글 수정 - 성공")
    void modify_tour_success() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        Set<String> tourThemes = new HashSet<>();

        tourThemes.add("SSG_LANDERS"); tourThemes.add("인천_신세계"); tourThemes.add("추신수"); tourThemes.add("인증샷");

        TourSave tourSave = new TourSave();
        tourSave.setMaximumAge(30);
        tourSave.setMinimumAge(20);
        tourSave.setRequiredGender(1);
        tourSave.setTourContent("믄학 고고");
        tourSave.setTourTitle("신세계 롯데 개막전!");
        tourSave.setTourEndDate("0401");
        tourSave.setTourStartDate("0401");
        tourSave.setTourLocation("인천");
        tourSave.setTourTeam("문학");
        tourSave.setTourDateDetail("드디어 2021 KBO 개막!");
        tourSave.setBio("인천 야구를 사랑합니당");
        tourSave.setTourThemes(tourThemes);
        tourSave.setMaximumMember(1);
        tourSave.setMaximumMember(10);

        Long id = 188L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/modify-tour/{id}", id).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSave)))
                .andDo(print())
                .andDo(document("modify-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("동행 글 id")
                        ),
                        requestFields(
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("동행 장소"),
                                fieldWithPath("tourTeam").type(JsonFieldType.STRING).description("동행 팀"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("동행 제목"),
                                fieldWithPath("tourContent").type(JsonFieldType.STRING).description("동행 시작일"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("동행 시작일"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("동행 종료일"),
                                fieldWithPath("requiredGender").type(JsonFieldType.NUMBER).description("성별 조건"),
                                fieldWithPath("tourTheme").type(JsonFieldType.STRING).description("동행 테마"),
                                fieldWithPath("minimumAge").type(JsonFieldType.NUMBER).description("최소 연령"),
                                fieldWithPath("maximumAge").type(JsonFieldType.NUMBER).description("최대 연령"),
                                fieldWithPath("totalNumberOfMember").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("nowNumberOfMember").type(JsonFieldType.NUMBER).description("현재 인원"),
                                fieldWithPath("tourDateDetail").type(JsonFieldType.STRING).description("일정에 대한 자세한 설명").optional(),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개").optional(),
                                fieldWithPath("tourThemes[]").type(JsonFieldType.ARRAY).description("동행 조건 - 테마").optional()
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("동행 글 수정 - 실패")
    void modify_tour_fail() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        TourSave tourSave = new TourSave();
        tourSave.setMaximumAge(30);
        tourSave.setMinimumAge(20);
        tourSave.setRequiredGender(1);
        tourSave.setTourContent("야구보고 술마실사람");
        tourSave.setTourTitle("고척 고고");
        tourSave.setTourEndDate("04-01");
        tourSave.setTourStartDate("04-01");
        tourSave.setTourLocation("서울");
        tourSave.setTourTeam("키움");

        Long id = 121L;

        mockMvc.perform(post("/modify-tour/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSave)))
                .andDo(print());
    }

    // api doc - 완료
    @Test
    @DisplayName("내 모집 내역")
    @WithMockUser
    void test_my_tour_list() throws Exception {
        Member member = new Member();
        member.setEmail("email0@email.com");
        member.setPassword("12345678ab!");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(authenticated().withUsername("nickname0"));

        mockMvc.perform(get("/api/my-tour-list").contextPath("/api")).andDo(print())
                .andDo(document("my-tour-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseBody()));
    }

    // api doc - 완료
    @Test
    @DisplayName("Tour 상세 페이지")
    @WithMockUser
    void tour_detail_test() throws Exception{

        Long id = 445L;
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tour-detail/{id}", id).contextPath("/api"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("tour-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Tour id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("Tour id"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("직관 지역"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("사용자의 팀"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("동행 시작일"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("동행 종료일"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("동행 모집 제목"),
                                fieldWithPath("tourContent").type(JsonFieldType.STRING).description("동행 모집 글 내용"),
                                fieldWithPath("requiredGender").type(JsonFieldType.NUMBER).description("동행 조건 - 성별"),
                                fieldWithPath("minimumAge").type(JsonFieldType.NUMBER).description("동행 조건 - 나이 - 최소"),
                                fieldWithPath("maximumAge").type(JsonFieldType.NUMBER).description("동행 조건 - 나이 - 최대"),
                                fieldWithPath("minimumMember").type(JsonFieldType.NUMBER).description("동행 조건 - 최소 인원"),
                                fieldWithPath("maximumMember").type(JsonFieldType.NUMBER).description("동행 조건 - 최대 인원"),
                                fieldWithPath("nowNumberOfMember").type(JsonFieldType.NUMBER).description("확정 인원"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("날씨"),
                                fieldWithPath("scrapCount").type(JsonFieldType.NUMBER).description("하트 수"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("gender").type(JsonFieldType.NUMBER).description("1 - 남성, 2 - 여성"),
                                fieldWithPath("tourDateDetail").type(JsonFieldType.STRING).description("일정에 대한 자세한 설명").optional(),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개").optional(),
                                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                fieldWithPath("tourThemes[]").type(JsonFieldType.ARRAY).description("동행 조건 - 테마").optional()
                        )
                ));
    }

    @Test
    @DisplayName("Tour 검색 목록 반환 - 팀명으로 검색 - 최신순")
    void tourSearch_test_byTeam_latest() throws Exception {
        TourSearch tourSearch = new TourSearch();
        tourSearch.setKeyword("NC");
        mockMvc.perform(post("/api/tour-search").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSearch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("tour-search-team",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestBody()
                ));

    }

    // api doc - 완료
    @Test
    @DisplayName("Tour 검색 목록 반환 - 지역으로 검색 - 최신순")
    void tourSearch_test_byLocation_latest() throws Exception {
        TourSearch tourSearch = new TourSearch();
        tourSearch.setKeyword("대구");
        mockMvc.perform(post("/api/tour-search").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSearch)))
                .andDo(print())
                .andDo(document("tour-search-location",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestBody(
                        )
                ));
    }

    // api doc - 완료
    @Test
    @DisplayName("Tour 전체 목록 반환 - 최신순")
    void show_all_tour_list() throws Exception {
        mockMvc.perform(get("/api/tour").contextPath("/api"))
                .andDo(print())
                .andDo(document("all-tour-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseBody()
                ));
    }

    @Test
    @DisplayName("Tour 전체 목록 반환 - 인기순")
    void show_all_tour_list_popular() throws Exception {
        mockMvc.perform(get("/api/tour-popular").contextPath("/api"))
                .andDo(print())
                .andDo(document("all-tour-list-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseBody()));
    }



    }*/

    @Test
    @WithMockUser
    void cancel_temp() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"))
                .andDo(print());

        mockMvc.perform(post("/api/cancel-temp").contextPath("/api"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("임시 저장 글 조회")
    void is_temp_article() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"))
                .andDo(print());

        mockMvc.perform(get("/api/temp-tour").contextPath("/api"))
                .andDo(print())
                .andDo(document("is-temp",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("exists").type(JsonFieldType.BOOLEAN).description("임시저장 글 존재 여부"),
                                fieldWithPath("tourId").type(JsonFieldType.NUMBER).description("임시저장 글 id")
                        )));
    }

   /* @Test
    @WithMockUser
    void test_temp_save() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname1"));

        Set<String> tourThemes = new HashSet<>();

        tourThemes.add("기아"); tourThemes.add("최강기아"); tourThemes.add("잠실"); tourThemes.add("인증샷"); tourThemes.add("먹방");

        LocalDate startDate = LocalDate.of(2021, 4, 3);
        LocalDate endDate = LocalDate.of(2021, 4, 5);

        TourSave tourSave = new TourSave();
        tourSave.setTourLocation("잠실");
        tourSave.setTourTeam("기아");
        tourSave.setTourStartDate(startDate);
        tourSave.setTourEndDate(endDate);
        tourSave.setTourTitle("기아 개막전 경기 같이가");
        tourSave.setTourContent("기아 개막전 경기 보러가요");
        tourSave.setRequiredGender(2);
        tourSave.setMinimumAge(20);
        tourSave.setMaximumAge(30);
        tourSave.setBio("최강기아");
        tourSave.setTourDateDetail("같이 인증사진 찍어주실분");
        // tourSave.setTourThemes(tourThemes);
        tourSave.setMinimumMember(1);
        tourSave.setMaximumMember(2);
        tourSave.setTempSaved(true);

        mockMvc.perform(post("/api/tour-upload").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSave)))
                .andDo(print())
                .andDo(document("tour-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("직관 지역"),
                                fieldWithPath("tourTeam").type(JsonFieldType.STRING).description("직관할 팀 - 삭제 예정이니 그냥 무시하셔도 됩니다"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("동행 시작일"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("동행 종료일"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("동행 모집 제목"),
                                fieldWithPath("tourContent").type(JsonFieldType.STRING).description("동행 모집 글 내용"),
                                fieldWithPath("requiredGender").type(JsonFieldType.NUMBER).description("동행 조건 - 성별"),
                                fieldWithPath("minimumAge").type(JsonFieldType.NUMBER).description("동행 조건 - 나이 - 최소"),
                                fieldWithPath("maximumAge").type(JsonFieldType.NUMBER).description("동행 조건 - 나이 - 최대"),
                                fieldWithPath("minimumMember").type(JsonFieldType.NUMBER).description("동행 조건 - 최소 인원"),
                                fieldWithPath("maximumMember").type(JsonFieldType.NUMBER).description("동행 조건 - 최대 인원"),
                                fieldWithPath("nowNumberOfMember").type(JsonFieldType.NUMBER).description("확정 인원"),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("자기소개"),
                                fieldWithPath("tourDateDetail").type(JsonFieldType.STRING).description("일정에 대한 자세한 설명"),
                                fieldWithPath("tourThemes[]").type(JsonFieldType.ARRAY).description("동행 조건 - 동행 테마").optional(),
                                fieldWithPath("tempSaved").type(JsonFieldType.BOOLEAN).description("임시 저장 여부")
                        )
                ));

    }*/

}