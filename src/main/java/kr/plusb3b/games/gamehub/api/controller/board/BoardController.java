package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;
import kr.plusb3b.games.gamehub.domain.game.dto.SummaryGamesDto;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final PostsService postsService;
    private final BoardRepository boardRepo;
    private final GameMetadataService gameMetadataService;
    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);4

//    @Value("${app.max.boardSize}")
//    String maxBoardSizeString;
//    int maxBoardSize = Integer.parseInt(maxBoardSizeString);


    public BoardController(BoardService boardService, PostsService postsService, BoardRepository boardRepo,
                           GameMetadataService gameMetadataService) {
        this.boardService = boardService;
        this.postsService = postsService;
        this.boardRepo = boardRepo;
        this.gameMetadataService = gameMetadataService;
    }

    @GetMapping
    public String boardMainPage(Model model) {
        Map<Board, List<Posts>> postsByBoard = boardService.loadTop5LatestPostsByBoard();
        model.addAttribute("postsByBoard", postsByBoard);

        //총 게시글 (totalPosts): 모든 게시판의 전체 게시글 수
        //활성 게시판 (totalBoards): 현재 활성화된 게시판 수
        //오늘 작성 (todayPosts): 오늘 작성된 게시글 수
        List<Integer> result = postsService.statsBoard();

        model.addAttribute("totalPosts", result.get(0));
        model.addAttribute("totalBoards", result.get(1));
        model.addAttribute("todayPosts", result.get(2));

        //게임게시판 데이터 Model 담기
        Map<Board, List<Games>> gamesByBoard = boardService.loadTop5LatestGamesByBoard();
        model.addAttribute("gamesByBoard", gamesByBoard);


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

    @GetMapping("/game-board/{boardId}/view")
    public String gameBoardMainPage(@PathVariable("boardId") String boardId ,Model model) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        List<SummaryGamesDto> summaryGames = gameMetadataService.getSummaryGame(boardId);

        model.addAttribute("summaryGames", summaryGames);
        model.addAttribute("board", board);

        return "board/common/post-games-list";
    }

}