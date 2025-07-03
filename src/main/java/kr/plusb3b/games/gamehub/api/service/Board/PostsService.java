package kr.plusb3b.games.gamehub.api.service.Board;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.*;
import kr.plusb3b.games.gamehub.api.dto.user.User;

import java.util.List;
import java.util.Map;

public interface PostsService {

    // 작성 & 수정
    void createPost(PostRequestDto postRequestDto, CreatePostsVO createPostsVO,
                    String boardId, HttpServletRequest request);
    void updatePost(PostRequestDto postRequestDto, String boardId, Long postId, HttpServletRequest request);

    // 조회

    //게시판 메뉴에서 간단하게 보여지는 용도
    List<SummaryPostDto> summaryPosts(String boardId);

    //전체적으로 보여지게 하는 용도
    Posts detailPosts(String boardId, Long postId);

    // 삭제
    boolean deactivatePost(Long postId, HttpServletRequest request);

    // 조회수
    void increaseViewCount(Long postId);

    //작성자와 로그인한 사용자가 같은가?
    boolean isAuthor(HttpServletRequest request, Posts posts);

}

