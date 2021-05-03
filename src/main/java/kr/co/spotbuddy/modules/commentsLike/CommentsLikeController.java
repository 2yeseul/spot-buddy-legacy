package kr.co.spotbuddy.modules.commentsLike;

import kr.co.spotbuddy.modules.commentsLike.dto.LikeList;
import kr.co.spotbuddy.modules.member.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentsLikeController {
    private final CommentsLikeService commentsLikeService;

    @PostMapping("/like/{id}")
    public void doLikeComment(@CurrentUser Object object, @PathVariable Long id) throws Exception {
        commentsLikeService.doLikeComment(object, id);
    }

    @PostMapping("/cancel-like/{id}")
    public void doCancelLike(@CurrentUser Object object, @PathVariable Long id) {
        commentsLikeService.doCancelLike(object, id);
    }

    @GetMapping("/like/my-list/{postId}")
    public List<LikeList> myLikeCommentList(@CurrentUser Object object, @PathVariable Long postId) {
        return commentsLikeService.getLikeCommentList(object, postId);
    }
}
