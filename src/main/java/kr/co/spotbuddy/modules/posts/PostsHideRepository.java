package kr.co.spotbuddy.modules.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.spotbuddy.infra.domain.PostsHide;

import java.util.List;

public interface PostsHideRepository extends JpaRepository<PostsHide, Long> {
    boolean existsByPostsIdAndMemberId(Long postsId, Long memberId);
    PostsHide findByPostsIdAndMemberId(Long postsId, Long memberId);

    List<PostsHide> findAllByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
}
