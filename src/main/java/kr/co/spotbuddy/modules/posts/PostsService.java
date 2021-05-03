package kr.co.spotbuddy.modules.posts;

import kr.co.spotbuddy.infra.domain.FileURL;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.posts.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import kr.co.spotbuddy.modules.comments.CommentsRepository;
import kr.co.spotbuddy.infra.config.words.Const;
import kr.co.spotbuddy.infra.domain.Posts;
import kr.co.spotbuddy.infra.domain.PostsHide;
import kr.co.spotbuddy.modules.response.IdResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostsService {
    private final MemberType memberType;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final FileURLRepository fileURLRepository;
    private final Uploader uploader;
    private final PostsHideRepository hideRepository;


    // 커뮤니티 글 수정
    @Transactional
    public void modifyPost(Long id, PostsModify postsModify, Object object) throws IOException {
        Member member = memberType.getMemberType(object);
        Posts posts = postsRepository.findById(id).get();

        // 글 작성한 사람일 때만 수정 가능
        if(member.equals(posts.getMember())) {
            boolean isAnonymous = postsModify.getIsAnonymous().equals("true");
            posts.update(postsModify.getTitle(), postsModify.getContent(), postsModify.getCategory(), isAnonymous);

            // 삭제하는 사진들이 있을 때
            if(postsModify.getDeleteFileNames() != null) {
                for(String url : postsModify.getDeleteFileNames()) {
                    if(url.equals("")) continue;
                    FileURL deleteFile = fileURLRepository.findByFileURL(url);
                    fileURLRepository.delete(deleteFile);
                }
            }

            // 새롭게 파일을 추가할 때
            if(postsModify.getMultipartFile()!=null)
                saveFiles(postsModify.getMultipartFile(), posts);

            postsRepository.save(posts);
        }
    }

    // 내가 쓴 글 조회
    public List<PostList> getMyPostList(Object object) {
        Member member = memberType.getMemberType(object);
        return getPostsList(postsRepository.findAllByMemberOrderByIdDesc(member));
    }

    // 검색
    public List<PostList> searchProcess(PostSearch postSearch) {
        List<PostList> postLists = new ArrayList<>();
        List<Posts> posts;

        // 홈에서 전체 검색
        if(postSearch.getTeamIndex() == -1)
            posts = postsRepository.searchByKeywordsAtHome(postSearch.getKeyword());

        // 구단 게시판 내 검색
        else
            posts = postsRepository.searchByKeywordsAtTeam(postSearch.getKeyword(), postSearch.getTeamIndex(), postSearch.getCategory());

        return convertPostsList(posts, postLists);
    }

    // 구단 게시판에서 조회 - not filtered
    public List<PostList> getTeamBoardPosts(Integer page, TeamRequest teamRequest) {
        int teamIndex = teamRequest.getTeamIndex();
        int category = teamRequest.getCategory();

        Page<Posts> postsPage = postsRepository.
                getPostsByTeam(teamIndex, category, PageRequest.of(page, 10, Sort.by("id").descending()));

        // Page -> List
        return getPostsList(postsPage.getContent());
    }

    // 구단 게시판에서 조회 - filtered
    public List<PostList> getFilteredTeamBoardPosts(Integer page, TeamRequest teamRequest, Object object) {
        List<Long> hideList = getMemberHideList(object);

        int teamIndex = teamRequest.getTeamIndex();
        int category = teamRequest.getCategory();

        if(hideList.size() == 0) {
            Page<Posts> postsPage = postsRepository.getPostsByTeam(teamIndex, category, PageRequest.of(page, 10, Sort.by("id").descending()));

            return getPostsList(postsPage.getContent());
        }

        Page<Posts> postsPage = postsRepository.getFilteredPosts(hideList, teamIndex, category, PageRequest.of(page, 10, Sort.by("id").descending()));

        return getPostsList(postsPage.getContent());
    }

    @NotNull
    private List<Long> getMemberHideList(Object object) {
        Member member = memberType.getMemberType(object);

        List<Long> hideList = new ArrayList<>();

        // 숨기기를 한 경우에만
        if(hideRepository.existsByMemberId(member.getId()))
            hideList = getHidePostsIdList(member);
        return hideList;
    }

    private List<Long> getHidePostsIdList(Member member) {
        List<PostsHide> postsHides = hideRepository.findAllByMemberId(member.getId());
        List<Long> hideList = new ArrayList<>();

        for(PostsHide postsHide : postsHides) {
            hideList.add(postsHide.getPostsId());
        }

        return hideList;
    }

    // 글 삭제
    @Transactional
    public void deletePost(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Posts posts = postsRepository.findById(id).get();

        if(member.equals(posts.getMember()))
            postsRepository.deleteById(id);
    }

    // 인기글 조회
    public List<PostList> getPopularPostsPage(Integer page) {
        Page<Posts> postsPage = postsRepository.findAll(PageRequest.of(page, 10, Sort.by("todayView").descending()));
        return getPostsList(postsPage.getContent());
    }

    // 인기글 조회 - 필터링
    public List<PostList> getFilteredPopularPostsPage(Integer page, Object object) {
        List<Long> hideList = getMemberHideList(object);

        Page<Posts> postsPage = postsRepository.getFilteredPopularPosts(hideList, PageRequest.of(page, 10, Sort.by("todayView").descending()));
        return getPostsList(postsPage.getContent());
    }

    @NotNull
    private List<PostList> getPostsList(List<Posts> content) {
        List<PostList> postsList = new ArrayList<>();

        return convertPostsList(content, postsList);
    }

    public List<PostList> convertPostsList(List<Posts> posts, List<PostList> postLists) {
        for(Posts post : posts) {
            int commentCount = commentsRepository.findAllByPosts(post).size();
            String photoUrl = "";
            // 사진이 존재 할 때만
            if(fileURLRepository.existsByPosts(post))
                photoUrl = fileURLRepository.findAllByPosts(post).get(0).getFileURL();

            PostList postList = PostList.builder()
                    .id(post.getId())
                    .postsDate(post.getPostsTime())
                    .viewCount(post.getViewCount())
                    .commentCount(commentCount)
                    .title(post.getTitle())
                    .content(post.getContent())
                    .teamIndex(post.getTeamIndex())
                    .photoUrl(photoUrl)
                    .category(post.getCategory())
                    .build();

            postLists.add(postList);
        }

        return postLists;
    }


    // 글 업로드
    @Transactional
    public IdResponse uploadPostProcess(Object object, PostSave postSave) throws IOException {
        Member member = memberType.getMemberType(object);
        Posts posts = savePost(postSave, member);
        if(postSave.getMultipartFile()!=null)
            saveFiles(postSave.getMultipartFile(), posts);

        return IdResponse.builder().id(posts.getId()).build();
    }

    // 글 조회
    public PostDetail getPostDetail(Long id) {
        Posts posts = postsRepository.findById(id).get();
        List<String> photos = new ArrayList<>();

        List<FileURL> fileURLS = fileURLRepository.findAllByPosts(posts);
        for(FileURL fileURL : fileURLS) {
            photos.add(fileURL.getFileURL());
        }

        plusViewCount(id);

        return postDetail(posts, photos);

    }

    public BadWordsResponse contentCheck(BadWordsRequest request) {
        if(isBadWordsIn(request.getContent()) || isBadWordsIn(request.getTitle())) {
            return BadWordsResponse.builder()
                    .badWordsIn(true)
                    .build();
        }

        else
            return BadWordsResponse.builder()
                    .badWordsIn(false)
                    .build();
    }

    private boolean isBadWordsIn (String content) {
        for(int i = 0; i < Const.BAD_WORDS.length; i++) {
            if(content.contains(Const.BAD_WORDS[i])) return true;
        }
        return false;
    }

    private void modifyPhotos(List<MultipartFile> multipartFiles, Posts posts) throws IOException {
            List<FileURL> fileURLS = fileURLRepository.findAllByPosts(posts);

            for (FileURL fileURL : fileURLS)
                fileURLRepository.delete(fileURL);

            // 새로 저장
            saveFiles(multipartFiles, posts);
    }

    // 조회수 증가
    private void plusViewCount(Long id) {
        Posts posts = postsRepository.findById(id).get();
        int previousViewCount = posts.getViewCount();
        int previousTodayView = posts.getTodayView();

        posts.setViewCount(previousViewCount + 1);
        posts.setTodayView(previousTodayView + 1);

        postsRepository.save(posts);
    }


    private PostDetail postDetail(Posts posts, List<String> photos) {
        int commentsCount = commentsRepository.findAllByPosts(posts).size();
        return PostDetail.builder()
                .id(posts.getId())
                .nickname(posts.getMember().getNickname())
                .isAnonymous(posts.isAnonymous())
                .postsTime(posts.getPostsTime())
                .viewCount(posts.getViewCount())
                .title(posts.getTitle())
                .content(posts.getContent())
                .commentsCount(commentsCount)
                .photoUrls(photos).build();
    }

    private Posts savePost(PostSave postSave, Member member) {
        boolean isAnonymous = postSave.getIsAnonymous().equals("true");

        Posts posts = Posts.builder()
                .member(member)
                .title(postSave.getTitle())
                .content(postSave.getContent())
                .isAnonymous(isAnonymous)
                .postsTime(LocalDateTime.now())
                .category(postSave.getCategory())
                .teamIndex(postSave.getTeamIndex())
                .build();

        return postsRepository.save(posts);
    }

    private void saveFiles(List<MultipartFile> fileList, Posts posts) throws IOException {
        if(fileList != null) {
            for (MultipartFile multipartFile : fileList) {
                String url = uploader.upload(multipartFile, "static");
                FileURL fileURL = FileURL.builder()
                        .fileURL(url)
                        .posts(posts)
                        .build();

                fileURLRepository.save(fileURL);
            }
        }
    }
}
