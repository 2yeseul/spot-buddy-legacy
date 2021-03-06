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
                                parameterWithName("id").description("????????? id")
                        ),
                        responseFields(
                                fieldWithPath("confirmed").type(JsonFieldType.BOOLEAN).description("?????? ?????? / true??? ?????? ?????? ??????")
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
                                parameterWithName("id").description("????????? id")
                        ),
                        responseFields(
                                fieldWithPath("myNickname").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("yourNickname").type(JsonFieldType.STRING).description("????????? ?????????")
                        )
                ));

    }

    @Test
    @WithMockUser
    @DisplayName("?????? ??????")
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
                          fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("????????? ?????????")
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
                                parameterWithName("id").description("????????? id")
                        ),
                        responseFields(
                                fieldWithPath("confirmed").type(JsonFieldType.BOOLEAN).description("?????? ??????")
                        )
                ));

    }


    // api doc ?????? ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ??????")
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
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("?????? ??? id")
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
                                parameterWithName("id").description("?????? ??? id")
                        ),
                        responseFields(
                                fieldWithPath("canceled").type(JsonFieldType.BOOLEAN).description("?????? ??????")
                        )
                ));
    }



    // api doc ?????? ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ?????? ??????")
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
                                fieldWithPath("tourLocation").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("tourTeam").type(JsonFieldType.STRING).description("?????? ???"),
                                fieldWithPath("tourTitle").type(JsonFieldType.STRING).description("?????? ??? ??????"),
                                fieldWithPath("tourStartDate").type(JsonFieldType.STRING).description("?????? ?????? ???"),
                                fieldWithPath("tourEndDate").type(JsonFieldType.STRING).description("?????? ?????? ???"),
                                fieldWithPath("nicknames").type(JsonFieldType.ARRAY).description("????????? ???????????? ?????????")
                        ))
                );
    }

    // api doc - ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ??????")
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


    // api doc - ??????
    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ?????????")
    void test_confirm_tour_list() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email","email1@email.com")
                .param("password", "12345678ab!"));

        mockMvc.perform(get("/my-confirm-tour"))
                .andDo(print());
    }

}