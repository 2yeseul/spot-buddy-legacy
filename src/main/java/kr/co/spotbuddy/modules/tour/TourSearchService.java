
package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.infra.domain.TourTheme;
import kr.co.spotbuddy.modules.tour.dto.TourList;
import kr.co.spotbuddy.modules.tour.dto.TourSearch;
import kr.co.spotbuddy.modules.tour.dto.TourThemeSearch;
import kr.co.spotbuddy.modules.tourTheme.TourThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourSearchService {

    private final TourSearchRepository tourSearchRepository;
    private final TourService tourService;
    private final TourThemeRepository tourThemeRepository;

    private static final List<String> PLACES = Arrays.asList("잠실", "고척", "문학", "수원", "대전", "광주", "사직", "대구", "창원");

    // 테마 검색
    public List<TourList> tourThemeSearch(TourThemeSearch tourThemeSearch) {
        String tourTheme = tourThemeSearch.getTourTheme();
        List<TourTheme> tourThemes = tourThemeRepository.findAllByTheme(tourTheme);
        List<Tour> tourList = getToursInfoByTourTheme(tourThemes);

        return tourService.makeTourList(tourList);
    }

    // 테마를 통해 조건에 맞는 동행 객체 불러오기
    private List<Tour> getToursInfoByTourTheme(List<TourTheme> tourThemes) {
        List<Tour> tourIdList = new ArrayList<>();

        for(TourTheme tourTheme : tourThemes) {
            tourIdList.add(tourTheme.getTour());
        }

        // 최신순으로 정렬
        tourIdList.sort(Comparator.comparing(Tour::getId).reversed());

        return tourIdList;
    }

    // 홈에서 검색
    public List<TourList> tourSearchAtHome(TourSearch tourSearch) {
        String keyword = "";
        if(tourSearch.getKeyword()!=null) keyword = tourSearch.getKeyword();

        log.info("keyword : " + keyword);
        List<Tour> tourList = new ArrayList<>();
        if(!keyword.equals(""))
            tourList = tourSearchRepository.searchByKeywordAtHome(keyword);

        return tourService.makeTourList(tourList);
    }

    // 동행 탭에서 검색 프로세스
    public List<TourList> tourSearchProcess(TourSearch tourSearch) {
        String keyword = tourSearch.getKeyword();
        LocalDate tourStartDate = tourSearch.getTourStartDate();
        LocalDate tourEndDate = tourSearch.getTourEndDate();
        return tourSearch(keyword, tourStartDate, tourEndDate);
    }

    private List<TourList> tourSearch(String keyword, LocalDate tourStartDate, LocalDate tourEndDate) {
        // 검색어가 null이 아닌 경우
        if(keyword != null) {
            String parsedKeyword = "";

            // 구장 parse
            for(String place : PLACES) if(keyword.contains(place)) parsedKeyword = place;

            List<Tour> tourList;
            // 구장 o 날짜 x
            if(tourStartDate == null) tourList = tourSearchRepository.searchByKeyword(parsedKeyword);

            // 구장 o 날짜 o
            else tourList = tourSearchRepository.searchByLocationAndDate(parsedKeyword, tourStartDate, tourEndDate);

            return tourService.makeTourList(tourList);
        }

        // 구장 x 날짜 o
        else {
            List<Tour> tourList = tourSearchRepository.searchByDate(tourStartDate, tourEndDate);
            return tourService.makeTourList(tourList);
        }
    }
}

