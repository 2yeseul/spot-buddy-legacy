package kr.co.spotbuddy.modules.posts;

import kr.co.spotbuddy.infra.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.spotbuddy.infra.domain.Posts;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Posts set todayView = 0")
    void resetTodayView();

    List<Posts> findAllByMemberOrderByIdDesc(Member member);

    Page<Posts> findAllByIdNotInAndTeamIndexAndCategory(List<Long> ids, int teamIndex, int category, Pageable pageable);

    Page<Posts> findAll(Pageable pageable);

    Page<Posts> findAllByIdNotIn(List<Long> ids, Pageable pageable);

    Page<Posts> findAllByTeamIndexAndCategory(int teamIndex, int category, Pageable pageable);

    Page<Posts> findAllByIdNotInAndTeamIndex(List<Long> ids, int teamIndex, Pageable pageable);

    // 전체 검색
    default List<Posts> searchByKeywordsAtHome(String keyword) {
        return findAllByTitleContainingOrContentContainingOrderByIdDesc(keyword, keyword);
    }

    List<Posts> findAllByTitleContainingOrContentContainingOrderByIdDesc(String title, String content);

    // 필터링 구단 글
    default Page<Posts> getFilteredPosts(List<Long> ids, int teamIndex, int category, Pageable pageable) {
        if(category == -1) {
            return findAllByIdNotInAndTeamIndex(ids, teamIndex, pageable);
        }
        return findAllByIdNotInAndTeamIndexAndCategory(ids, teamIndex, category, pageable);
    }

    // 필터링 인기글
    default Page<Posts> getFilteredPopularPosts(List<Long> ids, Pageable pageable) {
        if(ids.size() != 0)
            return findAllByIdNotIn(ids, pageable);
        else
            return findAll(pageable);
    }

    // 구단 게시판 내 검색
    default List<Posts> searchByKeywordsAtTeam(String keyword, int teamIndex, int category) {
        if(category == -1) {
            return findAllByTeamIndexAndTitleContainingOrTeamIndexAndContentContainingOrderByIdDesc(teamIndex, keyword, teamIndex, keyword);
        }
        return findAllByTeamIndexAndCategoryAndTitleContainingOrTeamIndexAndCategoryAndContentContainingOrderByIdDesc(teamIndex, category, keyword, teamIndex, category, keyword);
    }

    List<Posts> findAllByTeamIndexAndTitleContainingOrTeamIndexAndContentContainingOrderByIdDesc(int teamIndex1, String title, int teamIndex2, String content);

    List<Posts> findAllByTeamIndexAndCategoryAndTitleContainingOrTeamIndexAndCategoryAndContentContainingOrderByIdDesc(int teamIndex, int category, String title,
                                                                                                          int teamIndex2, int category2, String content);

    Page<Posts> findAllByTeamIndex(int teamIndex, Pageable pageable);

    // 구단 게시판 조회
    default Page<Posts> getPostsByTeam(int teamIndex, int category, Pageable pageable) {
        if(category == -1)
            return findAllByTeamIndex(teamIndex, pageable);

        else
            return findAllByTeamIndexAndCategory(teamIndex, category, pageable);
    }

}
