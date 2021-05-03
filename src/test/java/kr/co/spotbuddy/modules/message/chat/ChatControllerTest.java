package kr.co.spotbuddy.modules.message.chat;

import kr.co.spotbuddy.infra.domain.Chat;
import kr.co.spotbuddy.infra.domain.Tour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import kr.co.spotbuddy.modules.tour.TourRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    TourRepository tourRepository;


    @Autowired
    ChatRepository chatRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Test
    @WithMockUser
    void new_chat_room() throws Exception {

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "email2@email.com")
                .param("password", "12345678ab!"));

        Long tourId = 424L;
        Tour tour = tourRepository.findById(tourId).get();

        mockMvc.perform(post("/chat/new/424")).andDo(print());

    }

    @Test
    void delete_chat() throws Exception {
        Chat chat = chatRepository.findById(1730L).get();
        chatRepository.deleteById(1730L);
    }
}