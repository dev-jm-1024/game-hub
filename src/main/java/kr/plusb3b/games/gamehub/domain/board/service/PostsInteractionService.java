package kr.plusb3b.games.gamehub.domain.board.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;

public interface PostsInteractionService {

    //PostsReactionCount 객체 조립 및 삽입
    boolean savePostsReactionCount(Long postId, PostsReactionCountVO prcVO);

    //좋아요 누르기
    boolean likePost(User user, Long postId);

    //좋아요 취소
    boolean likePostCancel(User user, Long postId);

    //싫어요
    boolean dislikePost(User user, Long postId);

    //싫어요 취소
    boolean dislikePostCancel(User user, Long postId);

    //신고하기
    boolean reportPost(User user, Long postId);

    //신고하기 취소
    boolean reportPostCancel(User user, Posts posts);

    //조회수 증가
    boolean increaseViewCount(Long postId, HttpServletRequest request);

    //게시물의 좋아요 및 싫어요 등에 대한 정보
    PostsReactionCount getPostsReactionCount(Long postId);
}
