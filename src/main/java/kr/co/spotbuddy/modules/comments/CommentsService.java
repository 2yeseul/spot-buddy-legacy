package kr.co.spotbuddy.modules.comments;

import kr.co.spotbuddy.infra.domain.Comments;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.Posts;
import kr.co.spotbuddy.infra.domain.PushAlarmTF;
import kr.co.spotbuddy.infra.firebase.FirebaseCloudMessageService;
import kr.co.spotbuddy.modules.alarm.dto.AlarmForm;
import kr.co.spotbuddy.modules.comments.dto.CommentForm;
import kr.co.spotbuddy.modules.comments.dto.CommentModify;
import kr.co.spotbuddy.modules.comments.dto.CommentsList;
import kr.co.spotbuddy.modules.comments.dto.MyComments;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.posts.PostsRepository;
import kr.co.spotbuddy.modules.posts.dto.KEYWORDS;
import kr.co.spotbuddy.modules.pushAlarmTF.PushAlarmTFRepository;
import kr.co.spotbuddy.modules.token.TokenInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.spotbuddy.modules.alarm.AlarmService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;
    private final MemberType memberType;
    private final TokenInfoRepository tokenInfoRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final AlarmService alarmService;
    private final PushAlarmTFRepository pushAlarmTFRepository;

    private static final List<String> TEAMS = Arrays.asList(KEYWORDS.DOOSAN.getKeywords(), KEYWORDS.HANHWA.getKeywords(),
            KEYWORDS.NC.getKeywords(), KEYWORDS.LOTTE.getKeywords(), KEYWORDS.KIWOOM.getKeywords(), KEYWORDS.KIA.getKeywords(),
            KEYWORDS.KT.getKeywords(), KEYWORDS.SSG.getKeywords(), KEYWORDS.SAMSUNG.getKeywords(), KEYWORDS.LG.getKeywords());


    // 내 댓글 리스트
    public List<MyComments> getMyCommentList(Object object) {
        Member member = memberType.getMemberType(object);
        List<Comments> comments = commentsRepository.findAllByMemberOrderByIdDesc(member);

        List<MyComments> myComments = new ArrayList<>();

        convertToMyComments(comments, myComments);

        return myComments;
    }

    // comment list로 변환
    public void convertToMyComments(List<Comments> comments, List<MyComments> myComments) {
        for(Comments comment : comments) {
            Posts posts = comment.getPosts();
            MyComments myComment = MyComments.builder()
                    .postId(posts.getId())
                    .commentId(comment.getId())
                    .comment(comment.getComment())
                    .teamIndex(posts.getTeamIndex())
                    .postsTime(posts.getPostsTime())
                    .commentsTime(comment.getCommentsTime())
                    .viewCount(posts.getViewCount())
                    .commentCount(posts.getComments().size())
                    .title(posts.getTitle())
                    .build();

            myComments.add(myComment);
        }
    }

    // 댓글 달기
    @Transactional
    public CommentsList uploadComment(Object object, CommentForm commentForm) throws Exception {
        Member member = memberType.getMemberType(object);
        Posts posts = postsRepository.findById(commentForm.getPostId()).get();

        return saveComment(commentForm, member, posts);
    }

    // 댓글 저장
    private CommentsList saveComment(CommentForm commentForm, Member member, Posts posts) throws Exception {
        Long replyId = -1L;
        if(commentForm.isReplyStatus()) replyId = commentForm.getReplyId();

        Comments comments = Comments.builder()
                .member(member)
                .comment(commentForm.getComment())
                .commentsTime(LocalDateTime.now())
                .isAnonymous(commentForm.isAnonymous())
                .posts(posts)
                .isReply(commentForm.isReplyStatus())
                .replyId(replyId)
                .build();

        commentsRepository.save(comments);

        // TODO : 리팩토링
        // 알람 페이지 + 푸시알람
        if(!posts.getMember().equals(member)) {
            // 알람 저장
            saveNewAlarm(comments, comments.getPosts().getMember());
            // 알람 보내기
            pushAlarmToPostUploader(comments);
            if (commentForm.isReplyStatus()) {
                Comments mainComment = commentsRepository.findById(comments.getReplyId()).get();
                saveNewAlarm(comments, mainComment.getMember());
                pushAlarmToCommentUploader(comments);
            }
        }

        return CommentsList.builder()
                .commentId(comments.getId())
                .nickname(comments.getMember().getNickname())
                .likeCount(0)
                .isAnonymous(comments.isAnonymous())
                .comment(comments.getComment())
                .replyStatus(comments.isReply())
                .replyId(comments.getReplyId())
                .teamIndex(comments.getMember().getTeamIndex())
                .commentTime(comments.getCommentsTime())
                .build();
    }

    // 새 활동 알람 저장
    private void saveNewAlarm(Comments comments, Member receiver) {
        Posts posts = comments.getPosts();
        String title = TEAMS.get(posts.getTeamIndex() - 1) + " 게시판";
        String body = "새로운 댓글이 달렸습니다 : " + comments.getComment();

        AlarmForm alarmForm = AlarmForm.builder()
                .alarmType(3)
                .alarmObject(posts.getId())
                .title(title)
                .body(body)
                .build();

        alarmService.saveAlarmProcess(alarmForm, receiver);

    }

    // 글쓴 사람에게 알람
    private void pushAlarmToPostUploader(Comments comments) throws Exception {
        Member postUploader = comments.getPosts().getMember();
        commentPushAlarm(postUploader, comments);
    }

    // 답댓글
    private void pushAlarmToCommentUploader(Comments comments) throws Exception {
        Comments mainComment = commentsRepository.findById(comments.getReplyId()).get();
        Member alarmReceiver = mainComment.getMember();
        commentPushAlarm(alarmReceiver, comments);
    }

    private void commentPushAlarm(Member receiver, Comments comments) throws Exception {
        boolean activityTurnOn = false;
        if(pushAlarmTFRepository.existsByMember(receiver)) {
            PushAlarmTF pushAlarmTF = pushAlarmTFRepository.findByMember(receiver);
            activityTurnOn = pushAlarmTF.isActivityTurnOn();
        }

        // push alarm 설정 이력이 없을 때 -> default : true
        if(!pushAlarmTFRepository.existsByMember(receiver)) activityTurnOn = true;

        // ios 사용자이고, 활동 알람을 켜놓은 경우만
        if(tokenInfoRepository.existsByMember(receiver) && activityTurnOn) {
            String token = tokenInfoRepository.findByMember(receiver).getToken();
            int teamIndex = comments.getPosts().getTeamIndex();
            String title = TEAMS.get(teamIndex - 1) + " 게시판";
            String body = "새로운 댓글이 달렸습니다 : " + comments.getComment();
            Long postsId = comments.getPosts().getId();
            String path = "http://3.35.213.43/community/detail/" + teamIndex + "/" + postsId;

            firebaseCloudMessageService.sendMessageTo(token, title, body, path);
        }
    }

    // 댓글 수정
    public void modifyComment(Object object, CommentModify commentModify) {
        Member member = memberType.getMemberType(object);
        Comments comments = commentsRepository.findById(commentModify.getCommentId()).get();

        // 댓글 쓴 사람만 수정
        if(member.equals(comments.getMember())) {
            comments.modifyComment(commentModify.getComment());
            comments.modifyAnonymousStatus(commentModify.isAnonymous());
            commentsRepository.save(comments);
        }
    }

    // 댓글 리스트
    public List<CommentsList> getAllComments(Long id) {
        Posts posts = postsRepository.findById(id).get();

        // 전체 comment list
        List<Comments> comments = commentsRepository.findAllByPosts(posts);

        // dto list
        List<CommentsList> commentsLists = new ArrayList<>();

        // dto list로 변환
        convertList(comments, commentsLists);

        commentsLists.sort(Comparator.comparing(CommentsList::getReplyId));

        return commentsLists;
    }

    // 댓글 삭제
    public void deleteComment(Object object, Long id) {
        Member member = memberType.getMemberType(object);
        Comments comments = commentsRepository.findById(id).get();

        if(member.equals(comments.getMember()))
            commentsRepository.delete(comments);
    }

    private void convertList(List<Comments> comments, List<CommentsList> commentsLists) {
        for(Comments comment : comments) {
            int likeCount =0;

            Long replyId = comment.getId();

            // 답댓글인 경우
            if(comment.isReply()) replyId = comment.getReplyId();

            if(comment.getCommentsLikes()!=null) likeCount = comment.getCommentsLikes().size();

            CommentsList commentsList = CommentsList.builder()
                    .commentId(comment.getId())
                    .nickname(comment.getMember().getNickname())
                    .likeCount(likeCount)
                    .isAnonymous(comment.isAnonymous())
                    .comment(comment.getComment())
                    .commentTime(comment.getCommentsTime())
                    .replyStatus(comment.isReply())
                    .teamIndex(comment.getMember().getTeamIndex())
                    .replyId(replyId)
                    .build();

            commentsLists.add(commentsList);
        }
    }

}
