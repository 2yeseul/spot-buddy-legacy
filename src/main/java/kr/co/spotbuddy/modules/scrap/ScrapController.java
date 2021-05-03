package kr.co.spotbuddy.modules.scrap;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import kr.co.spotbuddy.modules.tour.dto.TourList;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScrapController {

    private final MemberType memberType;
    private final ScrapService scrapService;

    @PostMapping("/scrap/{id}")
    public String doScrap(@CurrentUser Object object, @PathVariable("id") Long id) {
        Member member = memberType.getMemberType(object);
        if(member == null) return "fail";
        else {
            scrapService.scrapProcess(id, member);
            return "success";
        }
    }

    @GetMapping("/my-scrap-list")
    public List<TourList> getMyScrapList(@CurrentUser Object object) {
        return scrapService.getScrapLists(object);
    }


    @PostMapping("/delete-scrap/{id}")
    public void deleteScrap(@PathVariable Long id, @CurrentUser Object object) {
        scrapService.deleteScrapProcess(id, object);
    }
}
