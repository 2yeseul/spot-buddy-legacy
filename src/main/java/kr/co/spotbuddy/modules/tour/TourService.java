package kr.co.spotbuddy.modules.tour;

import com.google.common.collect.Lists;
import kr.co.spotbuddy.infra.domain.*;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.member.TourDateDto;
import kr.co.spotbuddy.modules.posts.dto.KEYWORDS;
import kr.co.spotbuddy.modules.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import kr.co.spotbuddy.modules.block.BlockRepository;
import kr.co.spotbuddy.modules.photoUrl.ProfilePhotoRepository;
import kr.co.spotbuddy.modules.response.TempTourResponse;
import kr.co.spotbuddy.modules.response.IdResponse;
import kr.co.spotbuddy.modules.tour.dto.TourDetail;
import kr.co.spotbuddy.modules.tour.dto.TourList;
import kr.co.spotbuddy.modules.tour.dto.TourSave;
import kr.co.spotbuddy.modules.tourTheme.TourThemeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {

    private static final List<String> PLACES = Arrays.asList("서울", "인천", "수원", "대전", "광주", "부산", "대구", "창원");
    private static final List<String> TEAMS = Arrays.asList(KEYWORDS.SSG.getKeywords(), KEYWORDS.KIA.getKeywords(), KEYWORDS.DOOSAN.getKeywords(),
            KEYWORDS.HANHWA.getKeywords(), KEYWORDS.KIWOOM.getKeywords(), KEYWORDS.KT.getKeywords(), KEYWORDS.LG.getKeywords(),
            KEYWORDS.LOTTE.getKeywords(), KEYWORDS.SAMSUNG.getKeywords(), KEYWORDS.NC.getKeywords());

    private final TourRepository tourRepository;
    private final ScrapRepository scrapRepository;
    private final TourThemeRepository tourThemeRepository;
    private final MemberType memberType;
    private final BlockRepository blockRepository;
    private final ProfilePhotoRepository photoRepository;

    // 임시 저장 취소
    @Transactional
    public void deleteTempTourArticle(Member member) {
        List<Tour> tour = tourRepository.findAllByIsTempSavedAndMember(true, member);
        tourRepository.deleteAllByIsTempSavedAndMember(true, member);
    }

    // 임시 저장 동행 글 존재 여부
    public TempTourResponse getTempTourResponse(Member member) {
        boolean isExist = tourRepository.existsByIsTempSavedAndMember(true, member);
        // 임시 저장 글이 존재할 때
        if (isExist) {
            Long tourId = tourRepository.latestTempTour(member).getId();
            return TempTourResponse.builder()
                    .isExists(true)
                    .tourId(tourId)
                    .build();
        } else return TempTourResponse.builder().isExists(false).build();
    }


    // 동행 글 저장 - 최종 과정
    @Transactional
    public IdResponse uploadTourProcess(TourSave tourSave, Member member) {
        if (tourRepository.existsByIsTempSavedAndMember(true, member)) {
            Tour tour = tourRepository.latestTempTour(member);
            Long id = tour.getId();
            log.info("temp save id : " + tour.getId());
            modifyTour(id, tourSave, member);
            return IdResponse.builder().id(tour.getId()).build();
        }
        List<String> tourThemes = new ArrayList<>();
        if (tourSave.getTourThemes() != null)
            tourThemes = tourSave.getTourThemes();
        Tour tour = saveTour(tourSave, member);

        saveTourTheme(tourThemes, tour);

        return IdResponse.builder()
                .id(tour.getId()).build();
    }

    // 동행 글 삭제
    public void deleteTourArticle(Member member, Long id) {
        Tour tour = tourRepository.findById(id).get();
        // 글 쓴 사람만 삭제 가능
        if (member.equals(tour.getMember())) {
            tourRepository.deleteById(id);
        }
    }

    // 동행 마감
    @Transactional
    public void closeTourProcess(Object object, @PathVariable Long id) {
        Member member = memberType.getMemberType(object);
        Tour tour = tourRepository.findById(id).get();
        if (member.equals(tour.getMember())) {
            tour.closeTour();
            tourRepository.save(tour);
        }
    }

    // tour theme 저장
    private void saveTourTheme(List<String> themes, Tour tour) {
        for (String theme : themes) {
            TourTheme tourTheme = TourTheme.builder()
                    .tour(tour)
                    .theme(theme)
                    .build();

            tourThemeRepository.save(tourTheme);
        }
    }

    // tour theme 수정
    private void modifyThemes(List<String> tourThemes, Tour tour) {
        if (!tourThemes.isEmpty()) {
            List<TourTheme> tourThemeList = tourThemeRepository.findAllByTour(tour);
            for (TourTheme tourTheme : tourThemeList) {
                tourThemeRepository.delete(tourTheme);
            }

            for (String theme : tourThemes) {
                TourTheme tourTheme = TourTheme.builder()
                        .theme(theme)
                        .tour(tour)
                        .build();

                tourThemeRepository.save(tourTheme);
            }
        }
    }

    // 동행 글 수정
    @Transactional
    public void modifyTour(Long id, TourSave tourSave, Member member) {
        Tour tour = tourRepository.findById(id).get();

        tour.update(tourSave.getTourLocation(), tourSave.getTourTeam(), tourSave.getTourStartDate(), tourSave.getTourEndDate(), member,
                tourSave.getTourTitle(), tourSave.getTourContent(), tourSave.getRequiredGender(), tourSave.getMinimumAge(),
                tourSave.getMaximumAge(), tourSave.getTourDateDetail(), tourSave.getBio(), tourSave.getMinimumMember(),
                tourSave.getMaximumMember(), tourSave.isTempSaved());

        if (tourSave.getTourThemes() != null)
            modifyThemes(tourSave.getTourThemes(), tour);

        tourRepository.save(tour);
    }

    // 조회수 증가
    private void plusViewCount(Long id) {
        Tour tour = tourRepository.findById(id).get();
        int previousViewCount = tour.getViewCount();
        tour.setViewCount(previousViewCount + 1);
        tourRepository.save(tour);
    }

    // theme 리스트 불러오기
    private List<String> getTourThemes(Tour tour) {
        List<String> tourThemes = new ArrayList<>();

        // theme가 존재할 때만
        if (tourThemeRepository.existsByTour(tour)) {
            List<TourTheme> tourThemeList = tourThemeRepository.findAllByTour(tour);
            for (TourTheme tourTheme : tourThemeList) {
                tourThemes.add(tourTheme.getTheme());
            }
        }

        return tourThemes;
    }

    // TODO : 탈퇴 회원일 때는?
    // 동행 상세 페이지
    public TourDetail getTourDetail(Long id) {

        Tour tour = tourRepository.findById(id).get();
        Member member = new Member();
        if (tour.getMember() != null) member = tour.getMember();
        String nickname = member.getNickname();
        int weather = member.getWeather();
        int scrapCount = scrapRepository.findByTour(tour).size();
        int age = 2021 - Integer.parseInt(member.getBirth().substring(0, 4)) + 1;
        int gender = member.getGender();

        // 조회수 증가
        plusViewCount(id);

        return tourDetail(id, tour, nickname, weather, scrapCount, age, gender);
    }

    private TourDetail tourDetail(Long id, Tour tour, String nickname, int weather, int scrapCount, int age, int gender) {
        return TourDetail.builder()
                .id(id)
                .nickname(nickname)
                .tourTeam(tour.getTourTeam())
                .tourLocation(tour.getTourLocation())
                .teamIndex(tour.getMember().getTeamIndex())
                .tourStartDate(tour.getStartDate())
                .tourEndDate(tour.getEndDate())
                .nowNumberOfMember(tour.getNowNumberOfMember())
                .requiredGender(tour.getRequiredGender())
                .minimumAge(tour.getMinimumAge())
                .maximumAge(tour.getMaximumAge())
                .tourTitle(tour.getTourTitle())
                .tourContent(tour.getTourContent())
                .weather(weather)
                .scrapCount(scrapCount)
                .viewCount(tour.getViewCount())
                .age(age)
                .gender(gender)
                .tourDateDetail(tour.getTourDateDetail())
                .bio(tour.getBio())
                .tourThemes(getTourThemes(tour))
                .minimumMember(tour.getMinimumMember())
                .maximumMember(tour.getMaximumMember())
                .build();
    }

    public List<TourList> makeTourList(List<Tour> tours) {
        List<TourList> tourLists = new ArrayList<>();
        if (tours != null) {
            for (Tour tour : tours) {
                String nickname = tour.getMember().getNickname();
                // 모집이 끝나지 않은 경우 & 삭제 되지 않은 경우 & 임시 저장 아닌 경우
                if (!tour.isEnded() && !tour.isTempSaved() && !tour.isDeleteState()) {
                    int scrapCount = scrapRepository.findByTour(tour).size();

                    TourList tourList = TourList.builder()
                            .id(tour.getId())
                            .tourLocation(tour.getTourLocation())
                            .nickname(nickname)
                            .tourTitle(tour.getTourTitle())
                            .tourStartDate(tour.getStartDate())
                            .tourEndDate(tour.getEndDate())
                            .weather(tour.getMember().getWeather())
                            .scrapCount(scrapCount)
                            .viewCount(tour.getViewCount())
                            .build();

                    tourLists.add(tourList);
                }
            }
        }
        return tourLists;
    }

    private String getUploaderProfilePhoto(Tour tour, String photoUrl) {
        if (photoRepository.existsByMember(tour.getMember())) {
            ProfilePhoto profilePhoto = photoRepository.findByMember(tour.getMember());
            photoUrl = profilePhoto.getFileUrl();
        }
        return photoUrl;
    }

    public List<TourList> makePersonalTourList(List<Tour> tours) {
        List<TourList> tourLists = new ArrayList<>();
        for (int i = 0; i < tours.size(); i++) {

            Tour tour = tours.get(i);
            String nickname = tour.getMember().getNickname();

            // 모집이 끝난 경우에도 작성한 회원은 볼 수 있음
            int scrapCount = scrapRepository.findByTour(tour).size();

            // 임시 저장글은 보이면 안됨
            if (!tour.isTempSaved() && !tour.isDeleteState()) {
                TourList tourList = TourList.builder()
                        .id(tour.getId())
                        .tourLocation(tour.getTourLocation())
                        .nickname(nickname)
                        .tourTitle(tour.getTourTitle())
                        .tourStartDate(tour.getStartDate())
                        .tourEndDate(tour.getEndDate())
                        .weather(tour.getMember().getWeather())
                        .scrapCount(scrapCount)
                        .viewCount(tour.getViewCount())
                        .isEnded(tour.isEnded())
                        .tourState(myConfirmTourStatus(tour))
                        .build();

                tourLists.add(tourList);
            }
        }

        return tourLists;
    }

    public List<TourList> getMyTourList(Member member) {
        List<Tour> tours = tourRepository.myTourUploadList(member);
        return makePersonalTourList(tours);
    }

    // tour 목록 page 반환 (최신순)
    public List<TourList> getTourListPage(Integer page) {
        Page<Tour> tourPage = tourRepository.getAllTourPages(PageRequest.of(page, 10, Sort.by("id").descending()));
        List<Tour> tours = tourPage.getContent();
        return makeTourList(tours);
    }

    // tour 목록 최신순 차단 filter
    public List<TourList> getTourListPageFilter(Integer page, Member member) {
        // 차단 기록이 있는 경우
        if (blockRepository.existsByDoBlock(member)) {
            List<Block> blocks = blockRepository.findAllByDoBlock(member);

            List<Long> blockIds = new ArrayList<>();
            getBlockTourList(blocks, blockIds);

            Page<Tour> tourPage = tourRepository.getFilteredLatestPages(blockIds, PageRequest.of(page, 10, Sort.by("id").descending()));
            List<Tour> tours = tourPage.getContent();

            return makeTourList(tours);
        } else
            return getTourListPage(page);
    }

    // tour 목록 인기순 차단 filter
    public List<TourList> getPopularTourListPageFilter(int page, Member member) {
        // 차단 기록이 있는 경우
        if (blockRepository.existsByDoBlock(member)) {
            List<TourList> tourLists = getFilteredPopularList(member);

            return makePartitionPopularTourList(page, tourLists);
        } else
            return getPopularTourListPage(page);
    }

    private List<TourList> getFilteredPopularList(Member member) {
        List<Block> blocks = blockRepository.findAllByDoBlock(member);

        List<Long> blockIds = new ArrayList<>();
        getBlockTourList(blocks, blockIds);

        List<Tour> tours = tourRepository.getFilteredPopularList(blockIds);

        return makeTourList(tours);
    }

    private void getBlockTourList(List<Block> blocks, List<Long> blockIds) {
        for (Block block : blocks) {
            Member doBlock = block.getGetBlocked();
            List<Tour> tourIdList = tourRepository.findAllByMemberAndDeleteStateNot(doBlock, true);

            for (Tour tour : tourIdList) {
                Long id = tour.getId();
                blockIds.add(id);
            }
        }
    }

    // tour 목록 page 반환 (인기순)
    public List<TourList> getPopularTourListPage(int page) {
        List<Tour> tours = tourRepository.getAllTourList();
        List<TourList> tourList = makeTourList(tours);

        return makePartitionPopularTourList(page, tourList);
    }

    private List<TourList> makePartitionPopularTourList(int page, List<TourList> tourList) {
        tourList.sort(Comparator.comparing(TourList::getScrapCount).reversed());

        List<List<TourList>> subLists = Lists.partition(tourList, 10);

        if (page < subLists.size()) {
            return subLists.get(page);
        }
        else
            return new ArrayList<>();
    }



    // 새 동행 글 저장
    public Tour saveTour(TourSave tourSave, Member member) {
        Tour tour = Tour.builder()
                .member(member)
                .tourLocation(tourSave.getTourLocation())
                .tourTeam(tourSave.getTourTeam())
                .startDate(tourSave.getTourStartDate())
                .endDate(tourSave.getTourEndDate())
                .tourTitle(tourSave.getTourTitle())
                .tourContent(tourSave.getTourContent())
                .requiredGender(tourSave.getRequiredGender())
                .minimumAge(tourSave.getMinimumAge())
                .maximumAge(tourSave.getMaximumAge())
                .postsAt(LocalDateTime.now())
                .nowNumberOfMember(0) // 처음 업로 시에는 0
                .viewCount(0)
                .tourDateDetail(tourSave.getTourDateDetail())
                .bio(tourSave.getBio())
                .minimumMember(tourSave.getMinimumMember())
                .maximumMember(tourSave.getMaximumMember())
                .isTempSaved(tourSave.isTempSaved())
                .build();

        tourRepository.save(tour);

        return tour;
    }

    @Transactional
    public void updateNewDate(TourDateDto tourDateDto, Tour tour) {
        tour.updateTourDate(tourDateDto.getTourStartDate(), tourDateDto.getTourEndDate());
        tourRepository.save(tour);
    }

    // 내 동행 상태
    public String myConfirmTourStatus(Tour tour) {
        if(tour.getStartDate()!=null) {
            LocalDate today = LocalDate.now();
            LocalDate startDate = tour.getStartDate();
            LocalDate endDate = tour.getEndDate();

            if (today.isBefore(startDate))
                return "동행 예정";

            else if (isAfterOrEqual(startDate, today) && isBeforeOrEqual(endDate, today))
                return "동행 진행 중";
            else
                return "동행 완료";
        }

        return "";
    }

    private boolean isBeforeOrEqual(LocalDate date, LocalDate compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return !compareToDate.isAfter(date);
    }

    private boolean isAfterOrEqual(LocalDate date, LocalDate compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return !compareToDate.isBefore(date);
    }
}