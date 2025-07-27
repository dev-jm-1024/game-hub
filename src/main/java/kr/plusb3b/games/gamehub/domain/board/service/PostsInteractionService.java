package kr.plusb3b.games.gamehub.domain.board.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;

public interface PostsInteractionService {

    //PostsReactionCount 객체 조립 및 삽입
    boolean savePostsReactionCount(Long postId, PostsReactionCountVO prcVO);

    //좋아요 누르기
    boolean likePost(Long postId, String authUserId, HttpServletRequest request);

    //좋아요 취소
    boolean likePostCancel(Long postId, String authUserId, HttpServletRequest request);

    //싫어요
    boolean dislikePost(Long postId, String authUserId, HttpServletRequest request);

    //싫어요 취소
    boolean dislikePostCancel(Long postId, String authUserId, HttpServletRequest request);

    //신고하기
    boolean reportPost(Long postId, String authUserId, HttpServletRequest request);

    //신고하기 취소
    boolean reportPostCancel(Long postId, String authUserId, HttpServletRequest request);

    //조회수 증가
    boolean increaseViewCount(Long postId, HttpServletRequest request);
}
