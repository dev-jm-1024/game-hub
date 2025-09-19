package kr.plusb3b.games.gamehub.domain.board.service;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;

public interface PostsInteractionHelper {

    // 초기 반응 카운트 생성
    void createInitialReactionCount(Posts posts, PostsReactionCountVO prcVO);

    // 리액션 변경 (LIKE ↔ DISLIKE)
    void changeReaction(UserPostsReaction reaction);

    // 기존 리액션 취소
    void cancelUserReaction(UserPostsReaction reaction);

    // 리액션 카운트 증가
    void increaseReactionCount(UserPostsReaction reaction);

    // 리액션 카운트 감소
    void decreaseReactionCount(UserPostsReaction reaction);

    // 리액션 기록 생성
    UserPostsReaction recordReaction(User user, Posts posts, UserPostsReaction.ReactionType reactionType);
}