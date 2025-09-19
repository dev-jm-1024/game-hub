package kr.plusb3b.games.gamehub.application.board;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.repository.PostFilesRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateNoticeVO;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostContent;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostTitle;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.board.dto.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.vo.CreatePostsVO;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostsServiceImpl implements PostsService {

    private final BoardRepository boardRepo;
    private final PostsRepository postsRepo;
    private final AccessControlService access;
    private final FileUpload fileUpload;
    private final PostFilesRepository postFilesRepo;
    private final PostFilesService postFilesService;


    private final BoardService boardService;

    public PostsServiceImpl(BoardRepository boardRepo, PostsRepository postsRepo, AccessControlService access,
                            FileUpload fileUpload, PostFilesRepository postFilesRepo,
                            PostFilesService postFilesService,
                            BoardService boardService) {
        this.boardRepo = boardRepo;
        this.postsRepo = postsRepo;
        this.access = access;
        this.fileUpload = fileUpload;
        this.postFilesRepo = postFilesRepo;
        this.postFilesService = postFilesService;
        this.boardService = boardService;
    }

    @Override
    public Posts createPost(PostRequestDto postRequestDto,
                            String boardId, HttpServletRequest request) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        User user = access.getAuthenticatedUser(request);
        CreatePostsVO vo = new CreatePostsVO();

        return postsRepo.save( new Posts(
                board,
                user,
                PostTitle.of(postRequestDto.getPostTitle()),
                PostContent.of(postRequestDto.getPostContent()),
                vo.getViewCount(),
                LocalDate.now(),
                vo.getUpdatedAt(),
                vo.getPostAct(),
                vo.getImportantAct()
            )
        );
    }


    @Override
    public List<SummaryPostDto> summaryPosts(String boardId) {

        List<SummaryPostDto> result = postsRepo.findByBoard_BoardId(boardId).stream()
                .filter(Posts::isActivatePosts)
                .map(post -> new SummaryPostDto(
                        post.getBoard().getBoardId(),
                        post.getPostId(),
                        post.getUser().getMbNickName().getMbNickName(),
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
    @Transactional
    public boolean updatePost(PostRequestDto postRequestDto, String boardId, Long postId) {
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판이 존재하지 않음"));

        Posts posts = postsRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않음"));

        // 제목 & 내용 업데이트 (Dirty Checking)
        posts.changeTitle(PostTitle.of(postRequestDto.getPostTitle()));
        posts.changeContent(PostContent.of(postRequestDto.getPostContent()));
        posts.setUpdatedAt(LocalDate.now());

        //파일 업데이트
        handleFileUpdates(posts, postRequestDto);

        return true;
    }

    @Override
    public void increaseViewCount(Long postId) {
        //TODO: 조회수 증가 구현해야함 - 25.09.18
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


    @Override
    @Transactional
    public boolean deactivatePost(String boardId ,Long postId) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판이 존재하지 않음"));

        Posts posts = postsRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않음"));

        posts.changeDeactivatePost();

        return true;
    }

    @Override
    public List<Posts> getAllPosts() {
        return postsRepo.findAll(); // 빈 리스트도 그대로 반환
    }

    @Override
    public List<Posts> getPostsByBoardId(String boardId) {
        return  getAllPosts().stream()
                .filter(p -> p.getBoard().getBoardId().equals(boardId))
                .filter(Posts::isActivatePosts)
                .collect(Collectors.toList());
    }


    private void handleFileUpdates(Posts savedPosts, PostRequestDto dto) {
        // 기존 파일들 모두 삭제
        postFilesRepo.deletePostFilesByPostId(savedPosts.getPostId());

        // 새로운 파일이 있으면 업로드
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            Map<String, String> fileUrlMap = fileUpload.getFileUrlAndType(dto.getFiles());
            postFilesService.uploadPostFile(savedPosts, fileUrlMap);
        }
    }

}
