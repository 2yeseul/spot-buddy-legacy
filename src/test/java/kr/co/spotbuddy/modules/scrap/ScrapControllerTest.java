package kr.co.spotbuddy.modules.scrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.tour.TourRepository;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
class ScrapControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    ScrapService scrapService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    // api doc test
    @Test
    @WithMockUser
    @DisplayName("????????? ??????")
    void delete_my_scrap() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        Long id = 103L;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/delete-scrap/{id}", id).contextPath("/api"))
                .andDo(print())
                .andDo(document("delete-scrap",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? id")
                        )
                ));
    }


    @Test
    @WithMockUser
    @DisplayName("?????? ????????? ?????????")
    void get_my_scrap_list() throws Exception {

        Member member = new Member();
        member.setEmail("email0@email.com");
        member.setPassword("12345678ab!");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        mockMvc.perform(get("/my-scrap-list")).andDo(print())
                .andDo(document("my-scrap-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("?????? ??? id"),
                                fieldWithPath("[].tourLocation").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("?????? ??? ????????? ?????????"),
                                fieldWithPath("[].tourTitle").type(JsonFieldType.STRING).description("?????? ??? ??????"),
                                fieldWithPath("[].tourStartDate").type(JsonFieldType.STRING).description("?????? ?????? ???"),
                                fieldWithPath("[].tourEndDate").type(JsonFieldType.STRING).description("?????? ?????? ???"),
                                fieldWithPath("[].weather").type(JsonFieldType.NUMBER).description("?????? ??? ????????? ??????"),
                                fieldWithPath("[].scrapCount").type(JsonFieldType.NUMBER).description("?????? ??? ?????? ???"),
                                fieldWithPath("[].viewCount").type(JsonFieldType.NUMBER).description("?????? ??? ?????????")

                        )
                ));

    }

    @Test
    @DisplayName("????????? ?????????")
    @WithMockUser
    void do_scrap() throws Exception{

        Member member = new Member();
        member.setEmail("email70@email.com");
        member.setPassword("12345678ab!");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"))
                .andDo(print())
                .andExpect(authenticated().withUsername("nickname0"));

        Long id = 330L;
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/scrap/{id}" ,id).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("scrap",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? id")
                        )
                ));
   }

}