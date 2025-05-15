package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.board.TestDataBoard;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.atmosphere.config.service.Get;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostsRepository postsRepository;

    public BoardController(BoardRepository boardRepository, PostsRepository postsRepository) {
        this.boardRepository = boardRepository;
        this.postsRepository = postsRepository;
    }

    //전체 게시판 보여주는 페이지
    @GetMapping
    public String boardMainPage(Model model) {

        // 1. 전체 게시판 목록
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);

        // 2. 게시판별 Top 5 게시글 가져오기
        Map<Long, List<Posts>> postsByBoard = new HashMap<>();
        Pageable top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        for (Board board : boardList) {
            List<Posts> topPosts = postsRepository.findByBoardId(board.getBoard_id(), top5);
            postsByBoard.put(board.getBoard_id(), topPosts);
        }

        model.addAttribute("postsByBoard", postsByBoard);

        return "board/main-board";
    }

    //자유 게시판 경로 처리
    @GetMapping("/free")
    public String showFreeBoardPage(){
        return "board/post-list";
    }

    //공지사항 경로 처리
    @GetMapping("/notice")
    public String showNoticePage(){
        return "board/post-list";
    }

    //공략 게시판 경로 처리
    @GetMapping("/guide")
    public String showGuidePage(){
        return "board/post-list";
    }

    //Q&A 게시판 경로 처리
    @GetMapping("/qna")
    public String showQnaPage(){
        return "board/post-list";
    }

    //건의사항 게시판 경로 처리
    @GetMapping("/suggestion")
    public String showSuggestionPage(){
        return "board/post-list";
    }

    //신고 게시판 경로 처리
    @GetMapping("/report")
    public String showReportPage(){
        return "board/post-list";
    }

    //글 작성 페이지 경로 처리
    @GetMapping("/{boardId}/new")
    public String showPostPage(@PathVariable("boardId") int boardId){
        return "/board/common/write-content/post-form";
    }

    //게시물 읽기 페이지 처리 -- post-detail.html
    @GetMapping("/board/{boardId}/view")
    public String showPostDetailPage(@PathVariable("boardId") Long boardId, Model model){

        //게시물 데이터 보여주기
        Optional<Posts> postsData = postsRepository.findById(boardId);
        model.addAttribute("postsData", postsData);

        return "board/common/post-detail ";

    }

}
