package kr.co.spotbuddy.modules.scrapCommunity;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.ScrapCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.spotbuddy.infra.domain.Posts;

import java.util.List;

public interface ScrapCommunityRepository extends JpaRepository<ScrapCommunity, Long> {

    List<ScrapCommunity> findAllByMemberOrderByIdDesc(Member member);

    boolean existsByMemberAndPosts(Member member, Posts posts);

    ScrapCommunity findByMemberAndPosts(Member member, Posts posts);
}
