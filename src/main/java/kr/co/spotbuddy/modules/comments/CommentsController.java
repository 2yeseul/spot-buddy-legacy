package kr.co.spotbuddy.modules.comments;

import kr.co.spotbuddy.modules.comments.dto.CommentForm;
import kr.co.spotbuddy.modules.comments.dto.CommentModify;
import kr.co.spotbuddy.modules.comments.dto.CommentsList;
import kr.co.spotbuddy.modules.comments.dto.MyComments;
import kr.co.spotbuddy.modules.member.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 달기
    @PostMapping("/upload")
    public CommentsList doComment(@CurrentUser Object object, @RequestBody CommentForm commentForm) throws Exception {
        return commentsService.uploadComment(object, commentForm);
    }

    // 댓글 수정
    @PostMapping("/modify")
    public void modifyComment(@CurrentUser Object object, @RequestBody CommentModify commentModify) {
        commentsService.modifyComment(object, commentModify);
    }

    // 댓글 리스트
    @GetMapping("/list/{id}")
    public List<CommentsList> getComments(@PathVariable Long id) {
        return commentsService.getAllComments(id);
    }

    // 댓글 삭제
    @PostMapping("/delete/{id}")
    public void deleteComment(@CurrentUser Object object, @PathVariable Long id) {
        commentsService.deleteComment(object, id);
    }

    @GetMapping("/my-list")
    public List<MyComments> getMyCommentsList(@CurrentUser Object object) {
        return commentsService.getMyCommentList(object);
    }
}
