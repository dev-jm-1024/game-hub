package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserInteractionProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInteractionProviderImpl implements UserInteractionProvider {

    private final AccessControlService access;
    private final UserPostsReactionRepository userReactionRepo;
    private final PostsRepository postsRepo;

    public UserInteractionProviderImpl(AccessControlService access,
                                       UserPostsReactionRepository userReactionRepo,
                                       PostsRepository postsRepo) {
        this.access = access;
        this.userReactionRepo = userReactionRepo;
        this.postsRepo = postsRepo;
    }

    @Override
    public UserPostsReaction.ReactionType getUserReactionType(Posts posts, User user) {


        Optional<UserPostsReaction> reactionOpt = userReactionRepo.findByUserAndPosts(user, posts);
        if(reactionOpt.isPresent()) {
            return reactionOpt.get().getReactionType();
        }else{
            return UserPostsReaction.ReactionType.NONE;
        }
    }


}
