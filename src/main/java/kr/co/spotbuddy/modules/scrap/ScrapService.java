package kr.co.spotbuddy.modules.scrap;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Scrap;
import kr.co.spotbuddy.infra.domain.Tour;
import kr.co.spotbuddy.modules.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.block.BlockRepository;
import kr.co.spotbuddy.modules.tour.TourRepository;
import kr.co.spotbuddy.modules.tour.TourService;
import kr.co.spotbuddy.modules.tour.dto.TourList;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final TourRepository tourRepository;
    private final MemberType memberType;
    private final TourService tourService;
    private final BlockRepository blockRepository;

    // 스크랩 하기
    @Transactional
    public void scrapProcess(Long id, Member member) {
        Tour tour = tourRepository.findById(id).get();
        Scrap scrap = Scrap.builder()
                .member(member)
                .tour(tour)
                .build();
        if(scrapRepository.existsByMemberAndTour(member, tour)) {
            scrapRepository.delete(scrap);
        }
        else scrapRepository.save(scrap);
    }

    // 스크랩 리스트
    public List<TourList> getScrapLists(Object object) {
        Member member = memberType.getMemberType(object);
        List<Scrap> scrapList = scrapRepository.findAllByMemberOrderByIdDesc(member);
        List<Tour> tourList = new ArrayList<>();

        for(Scrap scrap : scrapList) {
            Tour tour = scrap.getTour();
            Member tourUploader = tour.getMember();

            if(!tour.isDeleteState() && !blockRepository.existsByDoBlockAndGetBlocked(member, tourUploader)) {
                tourList.add(tour);
            }
        }

        return tourService.makePersonalTourList(tourList);
    }

    // 스크랩 삭제
    public void deleteScrapProcess(Long id, Object object) {
        Tour tour = tourRepository.findById(id).get();
        Member member = memberType.getMemberType(object);
        Scrap scrap = scrapRepository.findByMemberAndTour(member, tour);
        scrapRepository.delete(scrap);
    }

}
