package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionHelper;
import kr.plusb3b.games.gamehub.domain.board.vo.CommentsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserCommentsReactionRepository;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CommentsInteractionHelperImpl implements CommentsInteractionHelper {

    private final CommentsReactionCountRepository commentsReactionCountRepo;
    private final UserCommentsReactionRepository userCommentsReactionRepo;

    public CommentsInteractionHelperImpl(CommentsReactionCountRepository commentsReactionCountRepo,
                                        UserCommentsReactionRepository userCommentsReactionRepo) {
        this.commentsReactionCountRepo = commentsReactionCountRepo;
        this.userCommentsReactionRepo = userCommentsReactionRepo;
    }

    @Override
    public void createInitialReactionCount(Comments comments, CommentsReactionCountVO vo) {
        commentsReactionCountRepo.save(new CommentsReactionCount(
                comments,
                vo.getLikeCount(),
                vo.getDislikeCount(),
                vo.getReportCount()
        ));
    }


    @Override
    public void changeReaction(UserCommentsReaction reaction) {

        UserCommentsReaction.ReactionType currentType = reaction.getReactionType();
        UserCommentsReaction.ReactionType newType = getOppositeReactionType(currentType);

        // DB 업데이트
        userCommentsReactionRepo.updateReactionTypeByCommentIdAndUserId(
                reaction.getComment().getCommentId(),
                reaction.getUser().getMbId(),
                newType
        );

        //메모리 객체도 업데이트
        reaction.setReactionType(newType);


    }

    @Override
    public void cancelUserReaction(UserCommentsReaction reaction) {
        userCommentsReactionRepo.delete(reaction);
        userCommentsReactionRepo.flush();

    }

    @Override
    public void increaseReactionCount(UserCommentsReaction reaction) {
        UserCommentsReaction.ReactionType reactionType = reaction.getReactionType();

        switch (reactionType) {
            case LIKE:
                commentsReactionCountRepo.incrementLikeCountByCommentId(reaction.getComment().getCommentId());
                break;
            case DISLIKE:
                commentsReactionCountRepo.incrementDislikeCountByCommentId(reaction.getComment().getCommentId());
                break;
            case REPORT:
                commentsReactionCountRepo.incrementReportCountByCommentId(reaction.getComment().getCommentId());
                break;
        }

    }

    @Override
    public void decreaseReactionCount(UserCommentsReaction reaction) {
        UserCommentsReaction.ReactionType reactionType = reaction.getReactionType();

        switch (reactionType) {
            case LIKE:
                commentsReactionCountRepo.decrementLikeCountByCommentId(reaction.getComment().getCommentId());
                break;
            case DISLIKE:
                commentsReactionCountRepo.decrementDislikeCountByCommentId(reaction.getComment().getCommentId());
                break;
            // REPORT는 감소 없음 (신고는 취소할 수 없다는 가정)
        }

    }

    @Override
    public UserCommentsReaction recordReaction(User user, Comments comments, UserCommentsReaction.ReactionType reactionType) {
        SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
        Long reactionId = sf.nextId();

        UserCommentsReaction savedReaction = userCommentsReactionRepo.save(new UserCommentsReaction(
                reactionId,
                user,
                comments,
                reactionType,
                LocalDate.now()
        ));

        userCommentsReactionRepo.flush();

        if (savedReaction == null) {
            throw new IllegalStateException("리액션 저장에 실패했습니다");
        }

        return savedReaction;
    }

    // === 유틸리티 메소드 ===
    private UserCommentsReaction.ReactionType getOppositeReactionType(UserCommentsReaction.ReactionType currentType) {
        return currentType == UserCommentsReaction.ReactionType.LIKE
                ? UserCommentsReaction.ReactionType.DISLIKE
                : UserCommentsReaction.ReactionType.LIKE;
    }
}
