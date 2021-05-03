package kr.co.spotbuddy.modules.appVersion;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionRequest;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionSave;
import kr.co.spotbuddy.modules.posts.S3UploaderImpl;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
class AppVersionControllerTest {

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
    @DisplayName("version save")
    void test_save_version() throws Exception {
        AppVersionSave save = new AppVersionSave();
        save.setToken("eAUf2IYZC00okE-zX0t37k:APA91bH3iIUfSZNGeWQYg_yQ97JhTOnwbwA9J7B438ApLzNYyM4zVP6pad46mRsRe6FTcksw7b3EN6LpgLkr33FV0WapXrefxWmAMxt0FBCb-jrJLqXIK7lFjNBpIOOxRuUm7zBKAhI9");
        save.setVersion("2.0");

        mockMvc.perform(post("/api/version/save").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(save)))
                .andDo(print())
                .andDo(document("version-save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("token").type(JsonFieldType.STRING).description("app token"),
                                fieldWithPath("version").type(JsonFieldType.STRING).description("현재 app version")
                        )
                ));
    }

    @Test
    @DisplayName("version get")
    void test_get_version() throws Exception {

        AppVersionRequest request = new AppVersionRequest();
        request.setToken("eAUf2IYZC00okE-zX0t37k:APA91bH3iIUfSZNGeWQYg_yQ97JhTOnwbwA9J7B438ApLzNYyM4zVP6pad46mRsRe6FTcksw7b3EN6LpgLkr33FV0WapXrefxWmAMxt0FBCb-jrJLqXIK7lFjNBpIOOxRuUm7zBKAhI9");


        mockMvc.perform(post("/api/version/get").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(document("version-get",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("token").type(JsonFieldType.STRING).description("app token")
                ),
                responseFields(
                        fieldWithPath("version").type(JsonFieldType.STRING).description("app version")
                )
        ));
    }
}