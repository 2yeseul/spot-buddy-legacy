package kr.co.spotbuddy.modules.scrap;

import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.tour.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ScrapServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ScrapService scrapService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TourRepository tourRepository;

}