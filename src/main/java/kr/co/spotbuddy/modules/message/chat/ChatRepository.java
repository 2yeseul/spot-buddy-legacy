package kr.co.spotbuddy.modules.message.chat;

import kr.co.spotbuddy.infra.domain.Chat;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Tour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Logger log = LoggerFactory.getLogger(ChatRepository.class);

    List<Chat> findAllByRequestMemberOrTourUploader(Member requestMember, Member tourUploader);

    default List<Chat> getMyList(Member member) {
        return findAllByRequestMemberOrTourUploader(member, member);
    }

    // 채팅방 존재 여부
    default boolean hasChatRoom(Member requestMember, Member tourUploader, Tour tour) {
        return existsByRequestMemberAndTourUploaderAndTour(requestMember, tourUploader, tour);
    }

    default Chat getChatRoom(Member requestMember, Member tourUploader, Tour tour) {
        return findByRequestMemberAndTourUploaderAndTour(requestMember, tourUploader, tour);
    }

    Chat findByRequestMemberAndTourUploaderAndTour(Member requestMember, Member tourUploader, Tour tour);

    boolean existsByRequestMemberAndTourUploaderAndTour(Member requestMember, Member tourUploader, Tour tour);

    boolean existsByRequestMemberAndTourUploader(Member requestMember, Member tourUploader);

    boolean existsByRequestMemberOrTourUploader(Member requestMember, Member tourUploader);

    boolean existsByTourUploaderAndIsTourUploaderRead(Member tourUploader, boolean tourUploaderState);

    boolean existsByRequestMemberAndIsRequestMemberReadOrTourUploaderAndIsTourUploaderRead(Member requestMember, boolean requestRead, Member tourUploader, boolean tourRead);

    boolean existsByRequestMemberAndIsRequestMemberRead(Member requestMember, boolean requestRead);

    boolean existsByTourUploader(Member member);

    boolean existsByRequestMember(Member member);

    default int sizeOfMessages(Chat chat) {
        return chat.getMessages().size();
    }

    default boolean isMemberReadChat(Member member) {
        if (existsByRequestMember(member) && !existsByTourUploader(member)) {
            log.info("1) only requestMember :" + !existsByRequestMemberAndIsRequestMemberRead(member, false));
            return !existsByRequestMemberAndIsRequestMemberRead(member, false);
        }
        else if (!existsByRequestMember(member) && existsByTourUploader(member)) {
            log.info("2) only tourUploader" + existsByTourUploaderAndIsTourUploaderRead(member, false));
            return existsByTourUploaderAndIsTourUploaderRead(member, false);
        }
        else if (existsByRequestMember(member) && existsByTourUploader(member)) {
            log.info("3) requestMember and tourUploader");
            if(existsByRequestMemberAndIsRequestMemberRead(member, false)) {
                log.info("3-1)");
                return !existsByRequestMemberAndIsRequestMemberRead(member, false);
            }
            else {
                log.info("3-2)");
                return !existsByTourUploaderAndIsTourUploaderRead(member, false);
            }
        }
        else {
            log.info("4) else");
            return !existsByRequestMember(member) && !existsByTourUploader(member);
        }
    }



    default boolean isReviewPossible(Member requestMember, Member tourUploader) {
        if(existsByRequestMemberAndTourUploader(requestMember, tourUploader)) return true;
        else return existsByRequestMemberAndTourUploader(tourUploader, requestMember);
    }
}
