package kr.co.spotbuddy.modules.commentsLike;

import kr.co.spotbuddy.infra.domain.*;
import kr.co.spotbuddy.modules.commentsLike.dto.LikeList;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.posts.PostsRepository;
import kr.co.spotbuddy.modules.token.TokenInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.alarm.AlarmService;
import kr.co.spotbuddy.modules.alarm.dto.AlarmForm;
import kr.co.spotbuddy.modules.comments.CommentsRepository;
import kr.co.spotbuddy.infra.firebase.FirebaseCloudMessageService;
import kr.co.spotbuddy.modules.posts.dto.KEYWORDS;
import kr.co.spotbuddy.modules.pushAlarmTF.PushAlarmTFRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsLikeService {

    private final CommentsLikeRepository commentsLikeRepository;
    private final CommentsRepository commentsRepository;
    private final MemberType memberType;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final TokenInfoRepository tokenInfoRepository;
    private final AlarmService alarmService;
    private final PostsRepository postsRepository;
    private final PushAlarmTFRepository pushAlarmTFRepository;

    private static final List<String> TEAMS = Arrays.asList(KEYWORDS.DOOSAN.getKeywords(), KEYWORDS.HANHWA.getKeywords(),
            KEYWORDS.NC.getKeywords(), KEYWORDS.LOTTE.getKeywords(), KEYWORDS.KIWOOM.getKeywords(), KEYWORDS.KIA.getKeywords(),
            KEYWORDS.KT.getKeywords(), KEYWORDS.SSG.getKeywords(), KEYWORDS.SAMSUNG.getKeywords(), KEYWORDS.LG.getKeywords());


    public List<LikeList> getLikeCommentList(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        List<LikeList> likeLists = new ArrayList<>();
        if(postsRepository.existsById(id)) {
            Posts posts = postsRepository.findById(id).get();
            List<Comments> commentsList = commentsRepository.findAllByPosts(posts);

            for (Comments comments : commentsList) {
                if (commentsLikeRepository.existsByMemberAndComments(member, comments)) {
                    LikeList likeList = LikeList.builder()
                            .commentId(comments.getId())
                            .build();

                    likeLists.add(likeList);
                }
            }
        }
        return likeLists;
    }

    // 좋아요
    @Transactional
    public void doLikeComment(Object object, Long id) throws Exception {
        Member member = memberType.getMemberType(object);
        Comments comments = commentsRepository.findById(id).get();

        // 좋아요 안되어있는 경우에만
        if(!commentsLikeRepository.existsByMemberAndComments(member, comments)) {
            CommentsLike commentsLike = CommentsLike.builder()
                    .member(member)
                    .comments(comments)
                    .build();

            commentsLikeRepository.save(commentsLike);

            // 내 댓글에 좋아요 하지 않은 경우만
            if(!member.equals(comments.getMember())) {
                saveNewAlarm(commentsLike);
                pushAlarmToCommentUploader(commentsLike);
            }
        }
    }

    // 좋아요 취소
    public void doCancelLike(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Comments comments = commentsRepository.findById(id).get();

        CommentsLike commentsLike = commentsLikeRepository.findByMemberAndComments(member, comments);

        // 좋아요를 누른 사람인 경우에만 삭제 가능
        if(member.equals(commentsLike.getMember()))
            commentsLikeRepository.delete(commentsLike);
    }

    // 새 활동 알람 저장
    private void saveNewAlarm(CommentsLike commentsLike) {
        Member commentUploader = commentsLike.getComments().getMember();
        String fromWhere = TEAMS.get(commentsLike.getComments().getPosts().getTeamIndex() - 1);
        String title = fromWhere + " 게시판";
        String nickname = commentsLike.getMember().getNickname();

        // 닉네임이 6글자 보다 길 때, 말줄임표 적용
        if(nickname.length() > 6) {
            nickname = nickname.substring(0, 6) + "...";
        }

        String body = nickname + "님이 당신의 댓글을 좋아합니다.";

        AlarmForm alarmForm = AlarmForm.builder()
                .alarmType(4)
                .alarmObject(commentsLike.getComments().getPosts().getId())
                .title(title)
                .body(body)
                .build();

        alarmService.saveAlarmProcess(alarmForm, commentUploader);
    }

    private void pushAlarmToCommentUploader(CommentsLike commentsLike) throws Exception {
        Member commentUploader = commentsLike.getComments().getMember();
        boolean activityTurnOn = false;

        if(pushAlarmTFRepository.existsByMember(commentUploader)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(commentUploader);
            activityTurnOn = pushAlarmTF.isActivityTurnOn();
        }

        // push alarm 설정 이력이 없을 때 -> default : true
        if(!pushAlarmTFRepository.existsByMember(commentUploader)) activityTurnOn = true;

        if(tokenInfoRepository.existsByMember(commentUploader) && activityTurnOn) {
            int teamIndex = commentsLike.getComments().getPosts().getTeamIndex();
            String fromWhere = TEAMS.get(teamIndex - 1);

            String token = tokenInfoRepository.findByMember(commentUploader).getToken();
            String title = fromWhere +" 게시판";
            String body = commentsLike.getMember().getNickname() + "님이 당신의 댓글을 좋아합니다.";
            Long postsId = commentsLike.getComments().getPosts().getId();
            String path = "http://3.35.213.43/community/detail/" + teamIndex + "/" + postsId;

            firebaseCloudMessageService.sendMessageTo(token, title, body, path);
        }
    }
}
