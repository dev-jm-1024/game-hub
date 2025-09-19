package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionHelper;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PostsInteractionHelperImpl implements PostsInteractionHelper {

    private final PostsReactionCountRepository postsReactionCountRepo;
    private final UserPostsReactionRepository userPostsReactionRepo;

    public PostsInteractionHelperImpl(PostsReactionCountRepository postsReactionCountRepo,
                                      UserPostsReactionRepository userPostsReactionRepo) {
        this.postsReactionCountRepo = postsReactionCountRepo;
        this.userPostsReactionRepo = userPostsReactionRepo;
    }

    @Override
    public void createInitialReactionCount(Posts posts, PostsReactionCountVO prcVO) {
        postsReactionCountRepo.save(new PostsReactionCount(
                posts,
                prcVO.getLikeCount(),
                prcVO.getDislikeCount(),
                prcVO.getReportCount()
        ));
    }

    @Override
    public void changeReaction(UserPostsReaction reaction) {
        UserPostsReaction.ReactionType currentType = reaction.getReactionType();
        UserPostsReaction.ReactionType newType = getOppositeReactionType(currentType);

        // DB 업데이트
        userPostsReactionRepo.updateReactionByPostIdAndUserId(
                reaction.getPosts().getPostId(),
                reaction.getUser().getMbId(),
                newType
        );

        // 메모리 객체도 업데이트
        reaction.setReactionType(newType);
    }

    @Override
    public void cancelUserReaction(UserPostsReaction reaction) {
        userPostsReactionRepo.delete(reaction);
        userPostsReactionRepo.flush();
    }

    @Override
    public void increaseReactionCount(UserPostsReaction reaction) {
        UserPostsReaction.ReactionType reactionType = reaction.getReactionType();

        switch (reactionType) {
            case LIKE:
                postsReactionCountRepo.incrementLikeCountByPostId(reaction.getPosts().getPostId());
                break;
            case DISLIKE:
                postsReactionCountRepo.incrementDislikeCountByPostId(reaction.getPosts().getPostId());
                break;
            case REPORT:
                postsReactionCountRepo.incrementReportCountByPostId(reaction.getPosts().getPostId());
                break;
        }
    }

    @Override
    public void decreaseReactionCount(UserPostsReaction reaction) {
        UserPostsReaction.ReactionType reactionType = reaction.getReactionType();

        switch (reactionType) {
            case LIKE:
                postsReactionCountRepo.decrementLikeCountByPostId(reaction.getPosts().getPostId());
                break;
            case DISLIKE:
                postsReactionCountRepo.decrementDislikeCountByPostId(reaction.getPosts().getPostId());
                break;
            // REPORT는 감소 없음 (신고는 취소할 수 없다는 가정)
        }
    }

    @Override
    public UserPostsReaction recordReaction(User user, Posts posts, UserPostsReaction.ReactionType reactionType) {
        SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
        Long reactionId = sf.nextId();

        UserPostsReaction savedReaction = userPostsReactionRepo.save(new UserPostsReaction(
                reactionId,
                user,
                posts,
                reactionType,
                LocalDate.now()
        ));

        userPostsReactionRepo.flush();

        if (savedReaction == null) {
            throw new IllegalStateException("리액션 저장에 실패했습니다");
        }

        return savedReaction;
    }

    // === 유틸리티 메소드 ===
    private UserPostsReaction.ReactionType getOppositeReactionType(UserPostsReaction.ReactionType currentType) {
        return currentType == UserPostsReaction.ReactionType.LIKE
                ? UserPostsReaction.ReactionType.DISLIKE
                : UserPostsReaction.ReactionType.LIKE;
    }

}