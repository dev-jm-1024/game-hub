package kr.plusb3b.games.gamehub.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;

public interface UserInteractionProvider {

    //1. 사용자가 어떠한 상태를 눌렀는지?
    UserPostsReaction.ReactionType getUserReactionType(Posts posts, User user);
}
