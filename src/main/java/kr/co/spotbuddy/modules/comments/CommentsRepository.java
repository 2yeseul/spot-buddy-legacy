package kr.co.spotbuddy.modules.comments;

import kr.co.spotbuddy.infra.domain.Comments;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    List<Comments> findAllByPosts(Posts posts);

    List<Comments> findAllByMemberOrderByIdDesc(Member member);
}
