package kr.plusb3b.games.gamehub.domain.board.service;

import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.vo.CommentsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;

public interface CommentsInteractionHelper {

    // 초기 반응 카운트 생성
    void createInitialReactionCount(Comments comments, CommentsReactionCountVO vo);

    //리액션 변경 (LIKE ↔ DISLIKE)
    void changeReaction(UserCommentsReaction reaction);

    // 기존 리액션 취소
    void cancelUserReaction(UserCommentsReaction reaction);

    // 리액션 카운트 증가
    void increaseReactionCount(UserCommentsReaction reaction);

    // 리액션 카운트 감소
    void decreaseReactionCount(UserCommentsReaction reaction);

    // 리액션 기록 생성
    UserCommentsReaction recordReaction(User user, Comments comments, UserCommentsReaction.ReactionType reactionType);
}
