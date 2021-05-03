package kr.co.spotbuddy.modules.confirmedTour;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.confirmedTour.dto.ChatRoomIdDto;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.tour.TourRepository;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class ConfirmedTourControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberType memberType;

    @Autowired
    ConfirmedTourRepository confirmedTourRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void test_is_all_user_confirmed() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"));

        Long id = 4445L;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/chat/all-confirm/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("chat-all-confirmed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채팅방 id")
                        ),
                        responseFields(
                                fieldWithPath("confirmed").type(JsonFieldType.BOOLEAN).description("확졍 여부 / true일 때만 모두 확정")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_this_chat_info() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"));

        Long id = 3835L;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/chat/info/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("chat-nickname-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채팅방 id")
                        ),
                        responseFields(
                                fieldWithPath("myNickname").type(JsonFieldType.STRING).description("나의 닉네임"),
                                fieldWithPath("yourNickname").type(JsonFieldType.STRING).description("상대방 닉네임")
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("동행 확정")
    void test_new_confirm() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "jessica6851@naver.com")
                .param("password", "12345678ab!"));

        ChatRoomIdDto chatRoomIdDto = new ChatRoomIdDto();
        chatRoomIdDto.setChatRoomId(3143L);

        Long id = 3141L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/confirm-tour/{id}", id).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRoomIdDto)))
                .andDo(print())
                .andDo(document("confirm-tour-new",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Tour id")
                        ),
                        requestFields(
                          fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 아이디")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_is_confirmed() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email65@email.com")
                .param("password", "12345678ab!"));


        Long id = 3143L;
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/chat/is-confirmed/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("is-confirmed-new",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채팅방 id")
                        ),
                        responseFields(
                                fieldWithPath("confirmed").type(JsonFieldType.BOOLEAN).description("확정 여부")
                        )
                ));

    }


    // api doc 작성 완료
    @Test
    @WithMockUser
    @DisplayName("동행 확정 취소")
    void test_cancel_confirm_tour() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"));

        Long id = 1790L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/cancel-tour/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("cancel-confirm-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Tour id"))
                ));

    }

    @Test
    @WithMockUser
    void test_my_cancel_list() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/cancel-tour/my-list").contextPath("/api"))
                .andDo(print())
                .andDo(document("cancel-tour-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("동행 글 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_is_canceled_tour() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email1@email.com")
                .param("password", "12345678ab!"));

        Long id = 1790L;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/cancel-state/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("cancel-tour-state",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("동행 글 id")
                        ),
                        responseFields(
                                fieldWithPath("canceled").type(JsonFieldType.BOOLEAN).description("취소 상태")
                        )
                ));
    }



    // api doc 작성 완료
    @Test
    @WithMockUser
    @DisplayName("동행 확정 목록 보기")
    void test_view_confirm_member() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email20@email.com")
                .param("password", "12345678ab!"));

        Long id = 1794L;
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/confirm-member/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("my-tour-confirm-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Tour id")
                        ),
                        responseFields(
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("동행 지역"),
                                fieldWithPath("tourTeam").type(JsonFieldType.STRING).description("동행 팀"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("동행 글 제목"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("동행 시작 일"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("동행 마감 일"),
                                fieldWithPath("nicknames").type(JsonFieldType.ARRAY).description("확정한 사람들의 닉네임")
                        ))
                );
    }

    // api doc - 완료
    @Test
    @WithMockUser
    @DisplayName("동행 확정")
    void test_confirm_tour() throws Exception {
        Member member = new Member();
        member.setEmail("email3@email.com");
        member.setPassword("12345678ab!");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email20@email.com")
                .param("password", "12345678ab!"));

        Long id = 1794L;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/confirm-tour/{id}", id)
                .contextPath("/api"))
                .andDo(print())
                .andDo(document("confirm-tour",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Tour id")
                        )));

    }


    // api doc - 완료
    @Test
    @WithMockUser
    @DisplayName("동행 참여 리스트")
    void test_confirm_tour_list() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email1@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/my-confirm-tour"))
                .andDo(print());
    }

}