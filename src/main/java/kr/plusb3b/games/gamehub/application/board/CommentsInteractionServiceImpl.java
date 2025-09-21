package kr.plusb3b.games.gamehub.application.board;

import jakarta.transaction.Transactional;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionHelper;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.vo.CommentsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserCommentsReactionRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserInteractionProvider;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CommentsInteractionServiceImpl implements CommentsInteractionService {

    private final CommentsRepository commentsRepo;
    private final UserCommentsReactionRepository userCommentsReactionRepo;
    private final UserInteractionProvider userInteractionProvider;
    private final UserRepository userRepo;
    private final CommentsInteractionHelper commentsInteractionHelper;

    public CommentsInteractionServiceImpl(CommentsRepository commentsRepo, UserCommentsReactionRepository userCommentsReactionRepo,
                                          UserInteractionProvider userInteractionProvider,
                                          UserRepository userRepo, CommentsInteractionHelper commentsInteractionHelper) {
        this.commentsRepo = commentsRepo;
        this.userCommentsReactionRepo = userCommentsReactionRepo;
        this.userInteractionProvider = userInteractionProvider;
        this.userRepo = userRepo;
        this.commentsInteractionHelper = commentsInteractionHelper;
    }

    @Override
    public boolean saveCommentsReactionCount(Long commentId, CommentsReactionCountVO crcVO) {

        try{
            Comments comments = commentsRepo.findById(commentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
            );

            commentsInteractionHelper.createInitialReactionCount(comments, crcVO);
            return true;

        } catch (Exception e) {
            System.out.println("댓글 반응 카운트 저장 실패: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean likeComment(User user, Long commentId) {

        try{

            //게시물 존재 확인
            Optional<Comments> commentOpt = commentsRepo.findById(commentId);
            if(commentOpt.isEmpty()) return false;

            Optional<UserCommentsReaction> reactOpt = userCommentsReactionRepo.findByUserAndComment(user, commentOpt.get());

            if(reactOpt.isEmpty()) {

                //케이스 1. 처음 좋아요 누르기
                return handleNewLikeReaction(user, commentOpt.get());
            }

            UserCommentsReaction react = reactOpt.get();
            return handleExistingReactionForLike(react);
        } catch (Exception e) {
            System.out.println("좋아요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean dislikeComment(User user, Long commentId) {

        try{

            // 게시물 존재 확인
            Optional<Comments> commentsOpt = commentsRepo.findById(commentId);
            if (commentsOpt.isEmpty()) return false;

            Optional<UserCommentsReaction> reactOpt = userCommentsReactionRepo.findByUserAndComment(user, commentsOpt.get());

            if (reactOpt.isEmpty()) {
                // 케이스 1: 처음 싫어요 누르기
                return handleNewDislikeReaction(user, commentsOpt.get());
            }

            UserCommentsReaction reaction = reactOpt.get();
            return handleExistingReactionForDislike(reaction);

        } catch (Exception e) {
            System.out.println("싫어요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }

@Override
@Transactional
public boolean reportComment(User user, Long commentId) {
    try {

        // 게시물 존재 확인
        Optional<Comments> commentsOpt = commentsRepo.findById(commentId);
        if (commentsOpt.isEmpty()) return false;


        // 1. 자신의 게시물 신고 방지
        if (user.getMbId().equals(commentsOpt.get().getUser().getMbId())) {
            return false;
        }

        // 2. 중복 신고 확인
        if(userInteractionProvider.getUserCommentsReportReactionType(commentsOpt.get(), user)) {
            return false;
        }

        // 3. 신고 처리
        return handleReportReaction(user, commentsOpt.get());

    } catch (Exception e) {
        System.out.println("신고 처리 실패: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}

    @Override
    @Transactional
    public boolean likeCommentCancel(User user, Long commentId) {
        // likeComment()에서 토글 처리하므로 동일하게 호출
        return likeComment(user, commentId);
    }

    @Override
    @Transactional
    public boolean dislikeCommentCancel(User user, Long commentId) {
        // dislikeComment()에서 토글 처리하므로 동일하게 호출
        return dislikeComment(user, commentId);
    }


    @Override
    @Transactional
    public boolean reportCommentCancel(User user, Comments comment) {
        return true;
    }

    @Override
    public CommentsReactionCount getCommentsReactionCount(Long commentId) {
        return commentsRepo.findById(commentId)
                .map(Comments::getReactionCount)
                .orElse(null);
    }


    // === 헬퍼 메소드들 ===

    //사용자의 리액션이 없는 경우 진행해주는 메소드 - 좋아요 ver
    private boolean handleNewLikeReaction(User user, Comments comments) {
        UserCommentsReaction newReaction = commentsInteractionHelper.recordReaction(
                user, comments, UserCommentsReaction.ReactionType.LIKE);
        commentsInteractionHelper.increaseReactionCount(newReaction);
        return true;
    }

    //사용자의 리액션이 없는 경우 진행해주는 메소드 - 싫어요 ver
    private boolean handleNewDislikeReaction(User user, Comments comments) {
        UserCommentsReaction newReaction = commentsInteractionHelper.recordReaction(
                user, comments, UserCommentsReaction.ReactionType.DISLIKE);
        commentsInteractionHelper.increaseReactionCount(newReaction);
        return true;
    }

    //사용자의 리액션이 "있는" 경우 진행해주는 메소드 - 좋아요 ver
    private boolean handleExistingReactionForLike(UserCommentsReaction reaction) {
        switch (reaction.getReactionType()) {
            case LIKE:
                // 좋아요 취소
                commentsInteractionHelper.decreaseReactionCount(reaction);
                commentsInteractionHelper.cancelUserReaction(reaction);
                break;

            case DISLIKE:
                // 싫어요 → 좋아요 변경
                commentsInteractionHelper.decreaseReactionCount(reaction);
                commentsInteractionHelper.changeReaction(reaction);

                // 메모리 객체 동기화
                reaction.setReactionType(UserCommentsReaction.ReactionType.LIKE);
                commentsInteractionHelper.increaseReactionCount(reaction);
                break;

            default:
                return false;
        }
        return true;
    }

    private boolean handleExistingReactionForDislike(UserCommentsReaction reaction) {
        switch (reaction.getReactionType()) {
            case DISLIKE:
                // 싫어요 취소
                commentsInteractionHelper.decreaseReactionCount(reaction);
                commentsInteractionHelper.cancelUserReaction(reaction);
                break;

            case LIKE:
                // 좋아요 → 싫어요 변경
                commentsInteractionHelper.decreaseReactionCount(reaction);
                commentsInteractionHelper.changeReaction(reaction);

                // 메모리 객체 동기화
                reaction.setReactionType(UserCommentsReaction.ReactionType.DISLIKE);
                commentsInteractionHelper.increaseReactionCount(reaction);
                break;

            default:
                return false;
        }
        return true;
    }

    private boolean handleReportReaction(User user, Comments comments) {
        // 신고 카운트 증가
        UserCommentsReaction reportReaction = commentsInteractionHelper.recordReaction(
                user, comments, UserCommentsReaction.ReactionType.REPORT);
        commentsInteractionHelper.increaseReactionCount(reportReaction);

        // 게시물 작성자의 신고 받은 횟수 증가
        User postAuthor = comments.getUser();
        postAuthor.increaseReportCnt();
        userRepo.save(postAuthor);
        userRepo.flush();

        return true;
    }

}