package kr.co.spotbuddy.modules.scrapCommunity;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.ScrapCommunity;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.posts.PostsRepository;
import kr.co.spotbuddy.modules.posts.PostsService;
import kr.co.spotbuddy.modules.scrapCommunity.dto.ScrapState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.infra.domain.Posts;
import kr.co.spotbuddy.modules.posts.dto.PostList;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapCommunityService {
    private final ScrapCommunityRepository scrapCommunityRepository;
    private final PostsRepository postsRepository;
    private final MemberType memberType;
    private final PostsService postsService;

    public ScrapState isMemberScrap(Long id, Object object) {
        Posts posts = postsRepository.findById(id).get();
        Member member = memberType.getMemberType(object);

        boolean scrapState = scrapCommunityRepository.existsByMemberAndPosts(member, posts);

        return ScrapState.builder().scrapState(scrapState).build();
    }

    @Transactional
    public void deleteScrapPost(Long id, Object object) {
        Posts posts = postsRepository.findById(id).get();
        Member member = memberType.getMemberType(object);

        if(scrapCommunityRepository.existsByMemberAndPosts(member, posts)) {
            ScrapCommunity scrapCommunity = scrapCommunityRepository.findByMemberAndPosts(member, posts);
            scrapCommunityRepository.delete(scrapCommunity);
        }
    }

    // 스크랩 하기
    @Transactional
    public void doScrapPosts(Long id, Object object) {
        Posts posts = postsRepository.findById(id).get();
        Member member = memberType.getMemberType(object);

        if(!scrapCommunityRepository.existsByMemberAndPosts(member, posts)) {
            ScrapCommunity scrapCommunity = ScrapCommunity.builder()
                    .member(member)
                    .posts(posts)
                    .build();

            scrapCommunityRepository.save(scrapCommunity);
        }
    }


    public List<PostList> getMyScrapList(Object object) {
        Member member = memberType.getMemberType(object);
        List<ScrapCommunity> scrapCommunities = scrapCommunityRepository.findAllByMemberOrderByIdDesc(member);
        List<Posts> posts = new ArrayList<>();

        for(ScrapCommunity scrapCommunity : scrapCommunities) {
            posts.add(scrapCommunity.getPosts());
        }

        List<PostList> postLists = new ArrayList<>();

        return postsService.convertPostsList(posts, postLists);
    }
}
