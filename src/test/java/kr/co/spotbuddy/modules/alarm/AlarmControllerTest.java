package kr.co.spotbuddy.modules.alarm;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.deleteAccount.dto.DeleteForm;
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
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;
import kr.co.spotbuddy.modules.tour.dto.TourSearch;
import kr.co.spotbuddy.modules.tourReview.TourReviewRepository;
import kr.co.spotbuddy.modules.tourReview.simpleTourReview.SimpleTourReviewRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class AlarmControllerTest {

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

    @Autowired
    AlarmRepository alarmRepository;

    @Autowired
    TourReviewRepository tourReviewRepository;

    @Autowired
    SimpleTourReviewRepository simpleTourReviewRepository;


    @Test
    @WithMockUser
    @DisplayName("전체 채팅방 삭제 테스트")
    void test_delete_all_chatRooms() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "20152917@sungshin.ac.kr")
                .param("password", "skdisk17!"));

        mockMvc.perform(post("/api/chat/delete/all").contextPath("/api"))
                .andDo(print())
                .andDo(document("chat-all-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("전체 알람 삭제 테스트")
    void test_delete_all_alarms() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jcab6851@naver.com")
                .param("password", "skdisk17!"));

        mockMvc.perform(post("/api/alarm/delete/all").contextPath("/api"))
                .andDo(print())
                .andDo(document("alarm-all-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void test_home_search_tour() throws Exception {
        String keyword = "ㅁㄴㅇ";

        TourSearch tourSearch = new TourSearch();

        tourSearch.setKeyword(keyword);

        mockMvc.perform(post("/api/tour-search/home").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tourSearch)))
                .andDo(print())
                .andDo(document("home-search-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("keyword").type(JsonFieldType.STRING).description("검색어"),
                                fieldWithPath("tourStartDate").description("").optional(),
                                fieldWithPath("tourEndDate").description("").optional()
                        )
                ));
    }


    @Test
    @WithMockUser
    void test_delete_chat() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "skdisk17!"));

        Long id = 4265L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/chat/delete/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("chat-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("chatRoom Id")
                        )
                ));
    }


    @Test
    @WithMockUser
    void test_my_alarm_status() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "skdisk17!"));

        mockMvc.perform(get("/api/alarm/read-status").contextPath("/api"))
                .andDo(print())
                .andDo(document("alarm-read-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("alarmRead").type(JsonFieldType.BOOLEAN).description("읽음 여부 : true - 전부읽음 , false - 안읽은 알람 존재")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_delete_alarm() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "skdisk17!"));

        Long id = 4251L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/alarm/delete/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("alarm-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("alarm Id")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_have_unread_alarm() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/alarm/read-status").contextPath("/api"))
                .andDo(print());
    }

    @Test
    void test_alarm_exists() throws Exception {
        Member member = memberRepository.findByNickname("이예슬이예슬");

        assertTrue(alarmRepository.existsByMemberAndReadStatus(member, false));
    }

    @Test
    @WithMockUser
    @DisplayName("내 알람 리스트")
    void test_get_my_alarm_list() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email65@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/api/alarm/my-list").contextPath("/api"))
                .andDo(print())
                .andDo(document("alarm-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].alarmId").type(JsonFieldType.NUMBER).description("알람 리스트 id"),
                                fieldWithPath("[].alarmType").type(JsonFieldType.NUMBER).description("알람 타입"),
                                fieldWithPath("[].alarmedObjectId").type(JsonFieldType.NUMBER).description("알람 받은 대상의 id"),
                                fieldWithPath("[].readStatus").type(JsonFieldType.BOOLEAN).description("읽음 상태"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("알람 출처"),
                                fieldWithPath("[].body").type(JsonFieldType.STRING).description("알람 내용"),
                                fieldWithPath("[].alarmDate").type(JsonFieldType.STRING).description("알람 온 시간")
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("알람 확인")
    void test_alarm_read() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email65@email.com")
                .param("password", "12345678ab!"));

        Long id = 2147L;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/alarm/read/{id}", id).contextPath("/api"))
                .andDo(document("alarm-read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("알람 리스트의 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("계정 삭제")
    void test_delete_account() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "pisok38522@asfalio.com")
                .param("password", "12345678ab!"));

        DeleteForm deleteForm = new DeleteForm();
        deleteForm.setReasonIndex(0);
        deleteForm.setEtc("탈퇴 자세한 이유");

        mockMvc.perform(post("/api/delete-member").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteForm)))
                .andDo(document("delete-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("reasonIndex").type(JsonFieldType.NUMBER).description("기타를 선택했을 때는 0, 나머지는 1부터 순서대로"),
                                fieldWithPath("etc").type(JsonFieldType.STRING).description("기타가 아닌 경우엔 empty 혹은 안보내주셔도 괜찮습니다")
                        )
                ));
    }

}