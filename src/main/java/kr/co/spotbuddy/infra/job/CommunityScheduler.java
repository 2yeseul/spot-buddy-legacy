package kr.co.spotbuddy.infra.job;

import kr.co.spotbuddy.modules.posts.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommunityScheduler {
    private final PostsRepository postsRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetPostsTodayVies() {
        log.info(">>>>> 인기글 조회수 리셋 >>>>>");
        postsRepository.resetTodayView();
    }
}
