package kr.co.spotbuddy.modules.tour;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.response.APIResponse;
import kr.co.spotbuddy.modules.response.IdResponse;
import kr.co.spotbuddy.modules.response.TempTourResponse;
import kr.co.spotbuddy.modules.tour.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
    private final MemberType memberType;
    private final TourRepository tourRepository;
    private final TourSearchService tourSearchService;

    // 테마검색
    @PostMapping("/tour-search/theme")
    public List<TourList> searchByTourTheme(@RequestBody TourThemeSearch tourThemeSearch) {
        return tourSearchService.tourThemeSearch(tourThemeSearch);
    }

    // 홈 검색
    @PostMapping("/tour-search/home")
    public List<TourList> searchTourAtHome(@RequestBody TourSearch tourSearch) {
        return tourSearchService.tourSearchAtHome(tourSearch);
    }

    // 임시저장 삭제
    @PostMapping("/cancel-temp")
    public void deleteTempTour(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        tourService.deleteTempTourArticle(member);
    }

    // 임시저장
    @GetMapping("/temp-tour")
    public TempTourResponse tempExists(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return tourService.getTempTourResponse(member);
    }

    // 동행 목록 - 최신순 - 필터링 O
    @GetMapping("/tour-list/filter/{page}")
    public List<TourList> getFilteredTourList(@PathVariable("page") @Min(0) Integer page, @CurrentUser Object object) throws Exception {
        Member member = memberType.getMemberType(object);
        return tourService.getTourListPageFilter(page, member);
    }

    // 동행 목록 - 최신순 - 필터링 X
    @GetMapping("/tour-list/{page}")
    public List<TourList> getTourListPages(@PathVariable("page") @Min(0) Integer page) throws Exception {
        return tourService.getTourListPage(page);
    }

    // 동행 목록 - 인기순 - 필터링 O
    @GetMapping("/tour-list/filter/popular/{page}")
    public List<TourList> getFilteredPopularTourListPages(@PathVariable("page") int page, @CurrentUser Object object) throws Exception {
        Member member = memberType.getMemberType(object);
        return tourService.getPopularTourListPageFilter(page, member);
    }

    // 동행 목록 - 인기순 - 필터링 X
    @GetMapping("/tour-list/popular/{page}")
    public List<TourList> getPopularTourListPages(@PathVariable("page") int page) throws Exception {
        return tourService.getPopularTourListPage(page);
    }

    // 내가 작성한 동행 목록
    @GetMapping("/my-tour-list")
    public List<TourList> getMyTourList(@CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return tourService.getMyTourList(member);
    }

    // 동행 검색 - 구장명으로
    @PostMapping("/tour-search")
    public List<TourList> getSearchTourList(@RequestBody TourSearch tourSearch) {
        return tourSearchService.tourSearchProcess(tourSearch);
    }

    // 동행 글 작성
    @PostMapping("/tour-upload")
    public IdResponse uploadTour(@RequestBody TourSave tourSave, @CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        return tourService.uploadTourProcess(tourSave, member);
    }

    // 동행 상세 페이지
    @GetMapping("/tour-detail/{id}")
    public TourDetail viewTourDetail(@PathVariable Long id) {
        return tourService.getTourDetail(id);
    }

    // TODO : 리팩토링
    // 동행 글 수정
    @PostMapping("/modify-tour/{id}")
    public APIResponse modifyTour(@PathVariable Long id, @RequestBody TourSave tourSave, @CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        Tour tour = tourRepository.findById(id).get();
        APIResponse response = new APIResponse();

        // 글 작성한 사람일 때만 수정 가능
        if(member.equals(tour.getMember())) {
            tourService.modifyTour(id, tourSave, member);
            response.setMessage("success");
        }

        else
            response.setMessage("fail");

        return response;
    }

    // 동행 글 삭제
    @PostMapping("/delete-tour/{id}")
    public void deleteTour(@PathVariable Long id, @CurrentUser Object object) {
        Member member = memberType.getMemberType(object);
        tourService.deleteTourArticle(member, id);
    }

    // 동행 마감
    @PostMapping("/close-tour/{id}")
    public void closeTour(@CurrentUser Object object, @PathVariable Long id) {
        tourService.closeTourProcess(object, id);
    }

}
