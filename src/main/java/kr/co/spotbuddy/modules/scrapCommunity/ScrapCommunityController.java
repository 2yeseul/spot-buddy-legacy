package kr.co.spotbuddy.modules.scrapCommunity;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.scrapCommunity.dto.ScrapState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import kr.co.spotbuddy.modules.posts.dto.PostList;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class ScrapCommunityController {
    private final ScrapCommunityService scrapCommunityService;

    @PostMapping("/scrap/{id}")
    public void doScrapPost(@PathVariable Long id, @CurrentUser Object object) {
        scrapCommunityService.doScrapPosts(id, object);
    }

    @GetMapping("/scrap/my-list")
    public List<PostList> getMyScrapList(@CurrentUser Object object) {
        return scrapCommunityService.getMyScrapList(object);
    }

    @PostMapping("/scrap/delete/{id}")
    public void doDeleteScrap(@PathVariable Long id, @CurrentUser Object object) {
        scrapCommunityService.deleteScrapPost(id, object);
    }

    @GetMapping("/scrap-state/{id}")
    public ScrapState isMemberScrap(@PathVariable Long id, @CurrentUser Object object) {
        return scrapCommunityService.isMemberScrap(id, object);
    }
}
