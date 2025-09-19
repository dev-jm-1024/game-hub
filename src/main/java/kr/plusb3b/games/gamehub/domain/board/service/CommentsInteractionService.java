package kr.plusb3b.games.gamehub.domain.board.service;

import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.vo.CommentsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;

public interface CommentsInteractionService {

    // CommentsReactionCount 객체 조립 및 삽입
    boolean saveCommentsReactionCount(Long commentId, CommentsReactionCountVO crcVO);

    // 좋아요 누르기
    boolean likeComment(User user, Long commentId);

    // 좋아요 취소
    boolean likeCommentCancel(User user, Long commentId);

    // 싫어요 누르기
    boolean dislikeComment(User user, Long commentId);

    // 싫어요 취소
    boolean dislikeCommentCancel(User user, Long commentId);

    // 댓글 신고하기
    boolean reportComment(User user, Long commentId);

    // 댓글 신고 취소
    boolean reportCommentCancel(User user, Comments comment);

    // 댓글 리액션 카운트 정보 조회
    CommentsReactionCount getCommentsReactionCount(Long commentId);
}
