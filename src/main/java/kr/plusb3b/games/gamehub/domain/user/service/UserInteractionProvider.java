package kr.plusb3b.games.gamehub.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;

public interface UserInteractionProvider {

    //1. 사용자가 어떠한 상태를 눌렀는지? - 게시물
    UserPostsReaction.ReactionType getUserPostsReactionType(Posts posts, User user);

    //2. 사용자가 어떠한 상태를 눌렀는지? - 댓글
    UserCommentsReaction.ReactionType getUserCommentsReactionType(Comments comments, User user);
}
