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
    @DisplayName("?????? ?????? ?????? ?????? - ????????? (page)")
    void tour_popular_list_page() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tour-list/popular/{page}", 0).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("tour-list-page-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("page").description("page ??????(0?????? ??????)")
                        )
                ));
    }

    // api doc ?????? ??????
    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ????????? (page)")
    void tour_list_page() throws Exception {
        mockMvc.perform(get("/api/tour-list/{page}", 0).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("tour-list-page",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("page").description("page ??????(0?????? ??????)")
                        ),
                        responseBody())
                );
    }

    // api doc ?????? ??????
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
                                parameterWithName("id").description("?????? ??? id")
                        ))
                );

    }

    // api doc ?????? ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ??? ?????? - ??????")
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
                                parameterWithName("id").description("?????? ??? id")
                        ))
                );
    }

    @Test
    @WithMockUser
    @DisplayName("?????? ?????? - ?????? - ?????? ?????? ?????? ")
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

    // api doc ?????? ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ??? ?????? - ??????")
    void modify_tour_success() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        Set<String> tourThemes = new HashSet<>();

        tourThemes.add("SSG_LANDERS"); tourThemes.add("??????_?????????"); tourThemes.add("?????????"); tourThemes.add("?????????");

        TourSave tourSave = new TourSave();
        tourSave.setMaximumAge(30);
        tourSave.setMinimumAge(20);
        tourSave.setRequiredGender(1);
        tourSave.setTourContent("?????? ??????");
        tourSave.setTourTitle("????????? ?????? ?????????!");
        tourSave.setTourEndDate("0401");
        tourSave.setTourStartDate("0401");
        tourSave.setTourLocation("??????");
        tourSave.setTourTeam("??????");
        tourSave.setTourDateDetail("????????? 2021 KBO ??????!");
        tourSave.setBio("?????? ????????? ???????????????");
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
                                parameterWithName("id").description("?????? ??? id")
                        ),
                        requestFields(
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("tourTeam").type(JsonFieldType.STRING).description("?????? ???"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("tourContent").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("requiredGender").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("tourTheme").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("minimumAge").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("maximumAge").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("totalNumberOfMember").type(JsonFieldType.NUMBER).description("??? ??????"),
                                fieldWithPath("nowNumberOfMember").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("tourDateDetail").type(JsonFieldType.STRING).description("????????? ?????? ????????? ??????").optional(),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("????????????").optional(),
                                fieldWithPath("tourThemes[]").type(JsonFieldType.ARRAY).description("?????? ?????? - ??????").optional()
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("?????? ??? ?????? - ??????")
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
        tourSave.setTourContent("???????????? ???????????????");
        tourSave.setTourTitle("?????? ??????");
        tourSave.setTourEndDate("04-01");
        tourSave.setTourStartDate("04-01");
        tourSave.setTourLocation("??????");
        tourSave.setTourTeam("??????");

        Long id = 121L;

        mockMvc.perform(post("/modify-tour/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSave)))
                .andDo(print());
    }

    // api doc - ??????
    @Test
    @DisplayName("??? ?????? ??????")
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

    // api doc - ??????
    @Test
    @DisplayName("Tour ?????? ?????????")
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
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("teamIndex").type(JsonFieldType.NUMBER).description("???????????? ???"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("tourContent").type(JsonFieldType.STRING).description("?????? ?????? ??? ??????"),
                                fieldWithPath("requiredGender").type(JsonFieldType.NUMBER).description("?????? ?????? - ??????"),
                                fieldWithPath("minimumAge").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? - ??????"),
                                fieldWithPath("maximumAge").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? - ??????"),
                                fieldWithPath("minimumMember").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? ??????"),
                                fieldWithPath("maximumMember").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? ??????"),
                                fieldWithPath("nowNumberOfMember").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("weather").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("scrapCount").type(JsonFieldType.NUMBER).description("?????? ???"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("??????"),
                                fieldWithPath("gender").type(JsonFieldType.NUMBER).description("1 - ??????, 2 - ??????"),
                                fieldWithPath("tourDateDetail").type(JsonFieldType.STRING).description("????????? ?????? ????????? ??????").optional(),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("????????????").optional(),
                                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("?????? ???"),
                                fieldWithPath("tourThemes[]").type(JsonFieldType.ARRAY).description("?????? ?????? - ??????").optional()
                        )
                ));
    }

    @Test
    @DisplayName("Tour ?????? ?????? ?????? - ???????????? ?????? - ?????????")
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

    // api doc - ??????
    @Test
    @DisplayName("Tour ?????? ?????? ?????? - ???????????? ?????? - ?????????")
    void tourSearch_test_byLocation_latest() throws Exception {
        TourSearch tourSearch = new TourSearch();
        tourSearch.setKeyword("??????");
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

    // api doc - ??????
    @Test
    @DisplayName("Tour ?????? ?????? ?????? - ?????????")
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
    @DisplayName("Tour ?????? ?????? ?????? - ?????????")
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
    @DisplayName("?????? ?????? ??? ??????")
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
                                fieldWithPath("exists").type(JsonFieldType.BOOLEAN).description("???????????? ??? ?????? ??????"),
                                fieldWithPath("tourId").type(JsonFieldType.NUMBER).description("???????????? ??? id")
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

        tourThemes.add("??????"); tourThemes.add("????????????"); tourThemes.add("??????"); tourThemes.add("?????????"); tourThemes.add("??????");

        LocalDate startDate = LocalDate.of(2021, 4, 3);
        LocalDate endDate = LocalDate.of(2021, 4, 5);

        TourSave tourSave = new TourSave();
        tourSave.setTourLocation("??????");
        tourSave.setTourTeam("??????");
        tourSave.setTourStartDate(startDate);
        tourSave.setTourEndDate(endDate);
        tourSave.setTourTitle("?????? ????????? ?????? ?????????");
        tourSave.setTourContent("?????? ????????? ?????? ????????????");
        tourSave.setRequiredGender(2);
        tourSave.setMinimumAge(20);
        tourSave.setMaximumAge(30);
        tourSave.setBio("????????????");
        tourSave.setTourDateDetail("?????? ???????????? ???????????????");
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
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("tourTeam").type(JsonFieldType.STRING).description("????????? ??? - ?????? ???????????? ?????? ??????????????? ?????????"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("tourContent").type(JsonFieldType.STRING).description("?????? ?????? ??? ??????"),
                                fieldWithPath("requiredGender").type(JsonFieldType.NUMBER).description("?????? ?????? - ??????"),
                                fieldWithPath("minimumAge").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? - ??????"),
                                fieldWithPath("maximumAge").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? - ??????"),
                                fieldWithPath("minimumMember").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? ??????"),
                                fieldWithPath("maximumMember").type(JsonFieldType.NUMBER).description("?????? ?????? - ?????? ??????"),
                                fieldWithPath("nowNumberOfMember").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("tourDateDetail").type(JsonFieldType.STRING).description("????????? ?????? ????????? ??????"),
                                fieldWithPath("tourThemes[]").type(JsonFieldType.ARRAY).description("?????? ?????? - ?????? ??????").optional(),
                                fieldWithPath("tempSaved").type(JsonFieldType.BOOLEAN).description("?????? ?????? ??????")
                        )
                ));

    }*/

}