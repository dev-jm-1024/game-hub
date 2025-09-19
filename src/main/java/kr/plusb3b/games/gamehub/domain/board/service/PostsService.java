package kr.plusb3b.games.gamehub.domain.board.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.CreateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.dto.UpdateNoticeDto;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateNoticeVO;
import kr.plusb3b.games.gamehub.domain.board.vo.CreatePostsVO;
import kr.plusb3b.games.gamehub.domain.board.dto.PostRequestDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostsService {

    // 작성 & 수정
    Posts createPost(PostRequestDto postRequestDto,
                     String boardId, HttpServletRequest request);

    boolean updatePost(PostRequestDto postRequestDto, String boardId, Long postId);

    // 조회
    List<Posts> getAllPosts();

    //특정 게시판의 게시물 가져오기
    List<Posts> getPostsByBoardId(String boardId);

    //게시판 메뉴에서 간단하게 보여지는 용도
    List<SummaryPostDto> summaryPosts(String boardId);

    // 조회수
    void increaseViewCount(Long postId);

    //작성자와 로그인한 사용자가 같은가?
    boolean isAuthor(HttpServletRequest request, Long postId);

    boolean validatePost(Long postId);

    boolean deactivatePost(String boardId, Long postId);

    //전체적으로 보여지게 하는 용도 - 사용자
    Posts detailPosts(String boardId, Long postId);



}

