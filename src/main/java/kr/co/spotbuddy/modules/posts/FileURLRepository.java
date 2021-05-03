package kr.co.spotbuddy.modules.posts;

import kr.co.spotbuddy.infra.domain.FileURL;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.spotbuddy.infra.domain.Posts;

import java.util.List;

public interface FileURLRepository extends JpaRepository<FileURL, Long> {
    List<FileURL> findAllByPosts(Posts posts);

    boolean existsByPosts(Posts posts);

    FileURL findByFileURL(String fileUrl);
}
