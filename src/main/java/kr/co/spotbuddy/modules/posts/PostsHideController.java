package kr.co.spotbuddy.modules.posts;

import kr.co.spotbuddy.modules.member.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import kr.co.spotbuddy.modules.posts.dto.HideResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostsHideController {
    private final PostsHideService postsHideService;

    // 게시글 숨기기
    @PostMapping("/hide/{id}")
    public void doHideCommunityPosts(@CurrentUser Object object, @PathVariable Long id) {
        postsHideService.hidePost(object, id);
    }

    // 숨기기 취소
    @PostMapping("/cancel-hide/{id}")
    public void cancelHideCommunityPosts(@CurrentUser Object object, @PathVariable Long id) {
        postsHideService.cancelHidePosts(object, id);
    }

    // 숨김 상태
    @GetMapping("/hide-status/{id}")
    public HideResponse isMemberHidePosts(@CurrentUser Object object, @PathVariable Long id) {
        return postsHideService.isMemberHidePosts(object, id);
    }
}
