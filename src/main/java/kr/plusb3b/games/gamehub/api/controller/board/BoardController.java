package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.board.TestDataBoard;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.atmosphere.config.service.Get;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostsRepository postsRepository;

    public BoardController(BoardRepository boardRepository, PostsRepository postsRepository) {
        this.boardRepository = boardRepository;
        this.postsRepository = postsRepository;
    }

    @GetMapping("/{boardId}")
    public String boardAllpage(@PathVariable Long boardId, Model model) {

        // 🔹 전체 게시판 목록
        List<Board> boardList = boardRepository.findAll();

        // 🔹 현재 선택한 게시판
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        // 🔹 선택된 게시판의 게시글 목록
        List<Posts> posts = postsRepository.findAllByBoard(board);

        model.addAttribute("boardList", boardList);     // 전체 게시판 목록
        model.addAttribute("board", board);             // 현재 게시판
        model.addAttribute("posts", posts);             // 게시글 목록
        model.addAttribute("boardId", boardId);         // URI 생성용

        return "board/list";
    }



    @GetMapping
    public String showBoardPage() {
        return "board/main-board";
    }

    //자유 게시판 경로 처리
    @GetMapping("/free")
    public String showFreeBoardPage(){
        return "board/free-board";
    }

    //공지사항 경로 처리
    @GetMapping("/notice")
    public String showNoticePage(){
        return "board/notice";
    }

    //공략 게시판 경로 처리
    @GetMapping("/guide")
    public String showGuidePage(){
        return "board/guide";
    }

    //Q&A 게시판 경로 처리
    @GetMapping("/qna")
    public String showQnaPage(){
        return "board/qna";
    }

    //건의사항 게시판 경로 처리
    @GetMapping("/suggestion")
    public String showSuggestionPage(){
        return "board/suggestion";
    }

    //신고 게시판 경로 처리
    @GetMapping("/report")
    public String showReportPage(){
        return "board/report";
    }

    //글 작성 페이지 경로 처리
    @GetMapping("/{boardId}/new")
    public String showPostPage(@PathVariable("boardId") int boardId){
        return "/board/common/write-content/post-form";
    }

}
