package kr.co.spotbuddy.modules.posts;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.infra.domain.PostsHide;
import kr.co.spotbuddy.modules.posts.dto.HideResponse;

@Service
@RequiredArgsConstructor
public class PostsHideService {
    private final PostsHideRepository hideRepository;
    private final MemberType memberType;

    @Transactional
    public void hidePost(Object object, Long id) {
        Member member = memberType.getMemberType(object);

        PostsHide postsHide = PostsHide.builder()
                .memberId(member.getId())
                .postsId(id)
                .build();

        hideRepository.save(postsHide);
    }

    public void cancelHidePosts(Object object, Long id) {
        Member member = memberType.getMemberType(object);

        // 숨기기를 했을 경우에만
        if(hideRepository.existsByPostsIdAndMemberId(id, member.getId())) {
            PostsHide postsHide = hideRepository.findByPostsIdAndMemberId(id, member.getId());
            hideRepository.delete(postsHide);
        }
    }

    public HideResponse isMemberHidePosts(Object object , Long id) {
        Member member = memberType.getMemberType(object);
        if(hideRepository.existsByPostsIdAndMemberId(id, member.getId())) {
            return HideResponse.builder()
                    .hideStatus(true)
                    .build();
        }

        return HideResponse.builder()
                .hideStatus(false)
                .build();
    }
}
