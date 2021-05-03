package kr.co.spotbuddy.modules.commentsLike;

import kr.co.spotbuddy.infra.domain.Comments;
import kr.co.spotbuddy.infra.domain.CommentsLike;
import kr.co.spotbuddy.infra.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsLikeRepository extends JpaRepository<CommentsLike, Long> {

    boolean existsByMemberAndComments(Member member, Comments comments);

    CommentsLike findByMemberAndComments(Member member, Comments comments);

}
