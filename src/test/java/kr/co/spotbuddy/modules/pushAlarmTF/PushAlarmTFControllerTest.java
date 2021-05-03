package kr.co.spotbuddy.modules.pushAlarmTF;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import kr.co.spotbuddy.modules.pushAlarmTF.dto.PushAlarmTypeStatus;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class PushAlarmTFControllerTest {

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
    void test_set_my_alarmStatus() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"));

        PushAlarmTypeStatus pushAlarmTypeStatus = new PushAlarmTypeStatus();
        pushAlarmTypeStatus.setPushAlarmType("ACTIVITY"); pushAlarmTypeStatus.setPushAlarmOn(true);

        mockMvc.perform(post("/api/push-alarm/setting").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pushAlarmTypeStatus)))
                .andDo(print())
                .andDo(document("push-alarm-setting",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("pushAlarmType").type(JsonFieldType.STRING).description("MESSAGE, ACTIVITY, SCHEDULE, PROMOTION"),
                                fieldWithPath("pushAlarmOn").type(JsonFieldType.BOOLEAN).description("켜짐 - true, 꺼짐 - false")
                        )
                ));
    }

    @Test
    @WithMockUser
    void test_get_my_alarm_status() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email0@email.com")
                .param("password", "12345678ab!"));


        mockMvc.perform(get("/api/push-alarm/my-status").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("push-alarm-my-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("messageTurnOn").type(JsonFieldType.BOOLEAN).description("메시지 수신 여부"),
                                fieldWithPath("activityTurnOn").type(JsonFieldType.BOOLEAN).description("활동 수신 여부"),
                                fieldWithPath("scheduleTurnOn").type(JsonFieldType.BOOLEAN).description("일정 수신 여부"),
                                fieldWithPath("promoTurnOn").type(JsonFieldType.BOOLEAN).description("프로모션 수신 여부")
                        )
                ));

    }
}