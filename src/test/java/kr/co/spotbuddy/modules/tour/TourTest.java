package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.modules.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class TourTest {

    @Autowired
    TourService tourService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TourRepository tourRepository;

    private static final List<String> PLACES = Arrays.asList("서울", "인천", "수원", "대전", "광주", "부산", "대구", "창원");
    private static final List<String> TEAMS = Arrays.asList("두산", "한화", "NC", "롯데", "키움", "KIA", "KT", "SK", "삼성", "LG");

    Random random = new Random();

/*
    @Test
    @DisplayName("테스트 디비 삽입")
    void insert_db(){
        for(int i=0;i<20;i++) {
            String nickname = "nickname" + i;
            TourSave tourSave = new TourSave();
            tourSave.setNickname(nickname);
            tourSave.setTourLocation(PLACES.get(random.nextInt(7)));
            tourSave.setTourTeam(TEAMS.get(random.nextInt(9)));
            tourSave.setTourStartDate(1010);
            tourSave.setTourEndDate(1012);
            tourSave.setTourTitle("tourTitle" + i);
            tourSave.setTourContent("tourContent" + i);
            tourSave.setRequiredGender(random.nextInt(2) + 1);
            tourSave.setTourTheme("tourTheme" + i);
            int minAge = random.nextInt(99) + 1;
            int maxAge = random.nextInt(99) +1;
            tourSave.setMinimumAge(min(minAge, maxAge));
            tourSave.setMaximumAge(max(minAge, maxAge));
            int totalNum = random.nextInt(99) + 1;
            int nowNum = random.nextInt(99) + 1;
            tourSave.setTotalNumberOfMember(max(totalNum, nowNum));
            tourSave.setNowNumberOfMember(min(totalNum, nowNum));

            tourService.saveNewTour(tourSave);
        }
    }


 */

}
