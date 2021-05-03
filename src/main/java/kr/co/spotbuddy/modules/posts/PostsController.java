package kr.co.spotbuddy.modules.posts;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.posts.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import kr.co.spotbuddy.modules.response.IdResponse;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostsController {

    private final PostsService postsService;
    private final MemberRepository memberRepository;

    // 비속어 체크
    @PostMapping("/content-check")
    public BadWordsResponse isContentContainsBadWords(@RequestBody BadWordsRequest badWordsRequest) {
        return postsService.contentCheck(badWordsRequest);
    }

    // 글 수정
    @PostMapping("/modify/{id}")
    public void modifyPost(@PathVariable Long id, @ModelAttribute PostsModify postsModify, @CurrentUser Object object) throws IOException {
        postsService.modifyPost(id, postsModify, object);
    }

    // 나의 글 리스트
    @GetMapping("/post/my-list")
    public List<PostList> getMyList(@CurrentUser Object object) {
        return postsService.getMyPostList(object);
    }

    // 글 업로드
    @PostMapping("/upload")
    public IdResponse uploadPost(@CurrentUser Object object, @ModelAttribute PostSave postSave) throws IOException {
        return postsService.uploadPostProcess(object, postSave);
    }

    // 검색
    @PostMapping("/search")
    public List<PostList> communitySearch(@RequestBody PostSearch postSearch) {
        return postsService.searchProcess(postSearch);
    }

    // 상세 페이지
    @GetMapping("/detail/{id}")
    public PostDetail getPostDetail(@PathVariable Long id) {
        return postsService.getPostDetail(id);
    }

    // 인기글 - not filtered
    @GetMapping("/popular/{page}")
    public List<PostList> getPopularPosts(@PathVariable("page") @Min(0) Integer page) {
        return postsService.getPopularPostsPage(page);
    }

    // 인기글 - filtered
    @GetMapping("/popular/filter/{page}")
    public List<PostList> getFilteredPopularPosts(@CurrentUser Object object, @PathVariable("page") @Min(0) Integer page) {
        return postsService.getFilteredPopularPostsPage(page, object);
    }

    // 구단 게시판 조회 - not filtered
    @PostMapping("/team/{page}")
    public List<PostList> getTeamBoardPosts(@PathVariable("page") @Min(0) Integer page, @RequestBody TeamRequest teamRequest) {
        return postsService.getTeamBoardPosts(page, teamRequest);
    }

    // 구단 게시판 조회 - filtered
    @PostMapping("/team/filter/{page}")
    public List<PostList> getFilteredTeamBoardPosts(@CurrentUser Object object, @PathVariable("page") @Min(0) Integer page, @RequestBody TeamRequest teamRequest) {
        return postsService.getFilteredTeamBoardPosts(page, teamRequest, object);
    }

    // 구단 게시판 회원 수
    @GetMapping("/team/count/{teamIndex}")
    public MemberCountResponse getCountOfTeamBoardMember(@PathVariable int teamIndex) {

        return MemberCountResponse.builder()
                .memberCount(memberRepository.countByTeamIndex(teamIndex))
                .build();
    }

    // 삭제
    @PostMapping("/delete/{id}")
    public void deletePost(@CurrentUser Object object, @PathVariable Long id) {
        postsService.deletePost(object, id);
    }

}
