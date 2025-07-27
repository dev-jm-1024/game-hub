package kr.plusb3b.games.gamehub.domain.board.service;

public interface CommentsInteractionService {

    //좋아요 누르기
    void likeComment(Long commentId, Long authUserId);

    //좋아요 취소
    void likeCommentCancel(Long commentId, Long authUserId);

    //싫어요
    void dislikeComment(Long commentId, Long authUserId);

    //싫어요 취소
    void dislikeCommentCancel(Long commentId, Long authUserId);

    //신고하기
    void reportComment(Long commentId, Long authUserId);

    //신고하기 취소
    void reportCommentCancel(Long commentId, Long authUserId);
}
