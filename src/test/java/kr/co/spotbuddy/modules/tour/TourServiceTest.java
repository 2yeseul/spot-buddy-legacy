package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.TourDateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class TourServiceTest {

    @Autowired
    TourService tourService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    MockMvc mockMvc;


    @Test
    @WithMockUser
    void tour_date_update() throws Exception {

        LocalDate startDate = LocalDate.of(2021, 4, 1);
        LocalDate endDate = LocalDate.of(2021, 4, 5);

        TourDateDto tourDateDto = TourDateDto.builder()
                .tourStartDate(startDate)
                .tourEndDate(endDate)
                .build();

        List<Tour> tourList = tourRepository.findAll();

        for(Tour tour : tourList) {
            tourService.updateNewDate(tourDateDto, tour);
        }
    }
}