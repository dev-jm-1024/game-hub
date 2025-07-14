package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.application.board.BoardServiceImpl;
import kr.plusb3b.games.gamehub.application.board.PostsServiceImpl;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.dto.PostsNotFoundException;
import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;
import kr.plusb3b.games.gamehub.domain.board.repository.PostFilesRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final PostFilesRepository postFilesRepo;
    private final BoardService boardService;
    private final PostsService postsService;
    private final AccessControlService access;
    private final BoardRepository boardRepo;
    private final CommentService commentService;
    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);4

//    @Value("${app.max.boardSize}")
//    String maxBoardSizeString;
//    int maxBoardSize = Integer.parseInt(maxBoardSizeString);


    public BoardController(PostFilesRepository postFilesRepo,  BoardService boardService,
                           PostsService postsService, AccessControlService access,
                           BoardRepository boardRepo, CommentService commentService) {
        this.postFilesRepo = postFilesRepo;
        this.boardService = boardService;
        this.postsService = postsService;
        this.access = access;
        this.boardRepo = boardRepo;
        this.commentService = commentService;
    }

    @GetMapping
    public String boardMainPage(Model model) {
        Map<Board, List<Posts>> postsByBoard = boardService.loadTop5LatestPostsByBoard();
        model.addAttribute("postsByBoard", postsByBoard);
        return "board/main-board";
    }



    //게시판 경로 처리
    @GetMapping("/{boardId}/view")
    public String dispatchBoardPost(@PathVariable("boardId") String boardId, Model model) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        List<SummaryPostDto> summaryPosts = postsService.summaryPosts(boardId);

        model.addAttribute("summaryPosts", summaryPosts);
        model.addAttribute("board", board); // ← 요거 추가!

        return "board/common/post-list";
    }




    // GlobalExceptionHandler
    @ExceptionHandler(PostsNotFoundException.class)
    public String handlePostNotFound(PostsNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "해당 게시글이 존재하지 않습니다.");
        model.addAttribute("errorCode", "ERR-POST-404");
        return "error/post-not-found"; // 사용자 친화적인 에러 페이지
    }



}