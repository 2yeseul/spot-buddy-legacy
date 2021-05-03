package kr.co.spotbuddy.modules.message;

import kr.co.spotbuddy.infra.domain.Chat;
import kr.co.spotbuddy.infra.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatOrderByIdDesc(Chat chat);

    // List<Message> findTopByOrderByChat(Chat chat);

    List<Message> findAllByChat(Chat chat);

    Message findTopByChatOrderByIdDesc(Chat chat);


}
