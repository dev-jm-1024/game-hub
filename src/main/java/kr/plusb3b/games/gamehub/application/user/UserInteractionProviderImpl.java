package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserCommentsReactionRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserInteractionProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInteractionProviderImpl implements UserInteractionProvider {

    private final UserPostsReactionRepository userPostsReactionRepo;
    private final UserCommentsReactionRepository userCommentsReactionRepo;


    public UserInteractionProviderImpl(UserPostsReactionRepository userPostsReactionRepo,
                                       UserCommentsReactionRepository userCommentsReactionRepo) {
        this.userPostsReactionRepo = userPostsReactionRepo;
        this.userCommentsReactionRepo = userCommentsReactionRepo;
    }

    @Override
    public UserPostsReaction.ReactionType getUserPostsReactionType(Posts posts, User user) {
        Optional<UserPostsReaction> reactionOpt = userPostsReactionRepo.findByUserAndPosts(user, posts);

        if(reactionOpt.isPresent()) {
            return reactionOpt.get().getReactionType();
        }else{
            return UserPostsReaction.ReactionType.NONE;
        }
    }

    @Override
    public boolean getUserPostsReportReactionType(Posts posts, User user) {
        Optional<UserPostsReaction> reactionOpt = userPostsReactionRepo.findByUserAndPosts(user, posts);

        if(reactionOpt.isPresent()) {
            if(reactionOpt.get().getReactionType() == UserPostsReaction.ReactionType.REPORT) {
                return true;
            }
        }

        return false;
    }

    //댓글 관련 로직 적어야함
    @Override
    public UserCommentsReaction.ReactionType getUserCommentsReactionType(Comments comments, User user) {
        Optional<UserCommentsReaction> reactionOpt = userCommentsReactionRepo.findByUserAndComment(user, comments);

        if(reactionOpt.isPresent()) {
            return reactionOpt.get().getReactionType();
        }else{
            return UserCommentsReaction.ReactionType.NONE;
        }
    }

    @Override
    public boolean getUserCommentsReportReactionType(Comments comments, User user) {
        Optional<UserCommentsReaction> reactionOpt = userCommentsReactionRepo.findByUserAndComment(user, comments);

        if(reactionOpt.isPresent()) {
            if(reactionOpt.get().getReactionType().equals(UserCommentsReaction.ReactionType.REPORT)) {
                return true;
            }
        }

        return false;
    }
}
