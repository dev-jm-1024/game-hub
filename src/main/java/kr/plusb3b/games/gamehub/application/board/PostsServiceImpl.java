package kr.plusb3b.games.gamehub.application.board;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.board.dto.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.vo.CreatePostsVO;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostsServiceImpl implements PostsService {

    private final BoardRepository boardRepo;
    private final PostsRepository postsRepo;
    private final AccessControlService access;

    public PostsServiceImpl(BoardRepository boardRepo, PostsRepository postsRepo, AccessControlService access) {
        this.boardRepo = boardRepo;
        this.postsRepo = postsRepo;
        this.access = access;
    }

    @Override
    public Posts createPost(PostRequestDto postRequestDto, CreatePostsVO createPostsVO,
                            String boardId, HttpServletRequest request) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        User user = access.getAuthenticatedUser(request);

        Posts posts = new Posts(
                board,
                user,
                postRequestDto.getPostTitle(),
                postRequestDto.getPostContent(),
                createPostsVO.getViewCount(),
                LocalDate.now(),
                createPostsVO.getUpdatedAt(),
                createPostsVO.getPostAct(),
                createPostsVO.getImportantAct()
        );

        return postsRepo.save(posts);
    }

    @Override
    public boolean updatePost(PostRequestDto postRequestDto, String boardId, Long postId){

        int result =  postsRepo.updatePostsByPostIdAndBoardId(
                postRequestDto.getPostTitle(),
                postRequestDto.getPostContent(),
                LocalDate.now(),
                postId,
                boardId
        );

        return result > 0;
    }

    @Override
    public List<SummaryPostDto> summaryPosts(String boardId) {

        List<SummaryPostDto> result = postsRepo.findByBoard_BoardId(boardId).stream()
                .filter(Posts::isActivatePosts)
                .map(post -> new SummaryPostDto(
                        post.getBoard().getBoardId(),
                        post.getPostId(),
                        post.getUser().getMbNickname(),
                        post.getPostTitle(),
                        post.getCreatedAt()
                        //post.getViewCount()
                ))
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public Posts detailPosts(String boardId, Long postId) {

        Posts result = postsRepo.findById(postId).stream()
                .filter(Posts::isActivatePosts)
                .findFirst()
                .orElseThrow(()-> new PostsNotFoundException("찾을 수 없음"));

        return result;


    }


    @Override
    public boolean deactivatePost(Long postId) {

        int deactivatePosts = postsRepo.deletePostsByPostId(postId);


        return deactivatePosts > 0;
    }

    @Override
    public void increaseViewCount(Long postId) {

    }

    @Override
    public boolean isAuthor(HttpServletRequest request, Long postId) {

        User user = access.getAuthenticatedUser(request);
        List<User> author = postsRepo.findById(postId).stream()
                .map(Posts::getUser)
                .collect(Collectors.toList());

        return author.get(0).equals(user);
    }

    @Override
    public boolean validatePost(Long postId) {
        if (postId == null) return false;
        return postsRepo.existsById(postId);
    }

    //총 게시글 (totalPosts): 모든 게시판의 전체 게시글 수
    //활성 게시판 (totalBoards): 현재 활성화된 게시판 수
    //오늘 작성 (todayPosts): 오늘 작성된 게시글 수
    @Override
    public List<Integer> statsBoard(){
        List<Integer> result = new ArrayList<>();

        int totalPosts = postsRepo.findAll().stream()
                .filter(Posts::isActivatePosts)
                .collect(Collectors.toList()).size();

        int totalBoards = boardRepo.findAll().stream()
                .filter(Board::isActivateBoard)
                .collect(Collectors.toList()).size();

        int todayPosts = postsRepo.findAll().stream()
                .filter(Posts::isActivatePosts)
                .filter(p -> p.getCreatedAt().equals(LocalDate.now()))
                .collect(Collectors.toList()).size();

        result.add(totalPosts);   // 인덱스 0
        result.add(totalBoards);  // 인덱스 1
        result.add(todayPosts);   // 인덱스 2

        return result;
    }
}
