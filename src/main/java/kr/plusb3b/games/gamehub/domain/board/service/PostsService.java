package kr.plusb3b.games.gamehub.domain.board.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.vo.CreatePostsVO;
import kr.plusb3b.games.gamehub.domain.board.dto.PostRequestDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;

import java.util.List;

public interface PostsService {

    // 작성 & 수정
    Posts createPost(PostRequestDto postRequestDto, CreatePostsVO createPostsVO,
                     String boardId, HttpServletRequest request);

    boolean updatePost(PostRequestDto postRequestDto, String boardId, Long postId);

    // 조회

    //게시판 메뉴에서 간단하게 보여지는 용도
    List<SummaryPostDto> summaryPosts(String boardId);

    //전체적으로 보여지게 하는 용도
    Posts detailPosts(String boardId, Long postId);

    // 삭제
    boolean deactivatePost(Long postId);

    // 조회수
    void increaseViewCount(Long postId);

    //작성자와 로그인한 사용자가 같은가?
    boolean isAuthor(HttpServletRequest request, Long postId);

    boolean validatePost(Long postId);

    //게시물 중요도 활성화

    //게시물 중요도 비활성화

    //게시물 통계
    List<Integer> statsBoard();

}

