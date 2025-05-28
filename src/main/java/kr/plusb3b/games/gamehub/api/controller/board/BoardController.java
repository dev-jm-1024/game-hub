package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.api.dto.board.*;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.atmosphere.config.service.Get;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostsRepository postsRepository;
    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);


    public BoardController(BoardRepository boardRepository, PostsRepository postsRepository) {
        this.boardRepository = boardRepository;
        this.postsRepository = postsRepository;
    }

    @GetMapping
    public String boardMainPage(Model model) {
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);

        Map<String, Boolean> hasPostsMap = new HashMap<>();
        Map<String, List<Posts>> postsByBoard = new HashMap<>();

        boolean isAnyPostExists = false;
        Pageable top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        for (Board board : boardList) {
            String boardId = board.getBoard_id();


            List<Posts> postList = postsRepository.findByBoard_BoardId(boardId);
            boolean hasPosts = postList.isEmpty();
            hasPostsMap.put(boardId, hasPosts);

            if (!hasPosts) {
                isAnyPostExists = true; // 전체 플래그 설정
                List<Posts> topPosts = postsRepository.findByBoard_BoardId(boardId, top5);
                postsByBoard.put(boardId, topPosts);
            }
        }

        model.addAttribute("hasPostsMap", hasPostsMap); // 게시판별 유무
        model.addAttribute("isPostsListEmpty", !isAnyPostExists); // 전체 플래그
        model.addAttribute("postsByBoard", postsByBoard);

        Page<Posts> latestPostsPage = postsRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("postsList", latestPostsPage.getContent());

        return "board/main-board";
    }


    //게시판 경로 처리
    @GetMapping("/{boardId}/view")
    public String dispatchBoardPost(@PathVariable String boardId, Model model) {

        // 1. 게시판 정보 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시판을 찾을 수 없습니다."));

        // 2. 게시글 목록 조회
        List<Posts> postsList = postsRepository.findByBoard_BoardId(boardId);
        boolean isPostsListEmpty = postsList.isEmpty();

        // 3. 모델에 데이터 추가
        model.addAttribute("board", board);  // Optional이 아닌 실제 객체로 넘김
        model.addAttribute("postsList", postsList);
        model.addAttribute("checkPostsList", isPostsListEmpty); // Thymeleaf에서 게시물 유무 표시용

        return "board/common/post-list";
    }


    //글 작성 페이지 경로 처리
    @GetMapping("/new")
    public String showPostPage(@RequestParam("boardId") String boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/common/post-form";
    }


    // GlobalExceptionHandler
    @ExceptionHandler(PostsNotFoundException.class)
    public String handlePostNotFound(PostsNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "해당 게시글이 존재하지 않습니다.");
        model.addAttribute("errorCode", "ERR-POST-404");
        return "error/post-not-found"; // 사용자 친화적인 에러 페이지
    }


    // /boards/{boardId}/{postsId}/view
    @GetMapping("/{boardId}/{postId}/view")
    public String showPostsViewPage(@PathVariable("boardId") String boardId,
                                    @PathVariable("postId") Long postId,Model model){

        //게시물이 보여서 링크타고 들어오면 당연히 게시물 데이터가 존재함.
        //근데 만약 없는 경우? 이건 내부적으로 예외처리를 해야한다.
        Posts posts = postsRepository.findByBoard_BoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new PostsNotFoundException(postId));

        //View 에다가 데이터 전송
        model.addAttribute("postsData", posts);
        return "/board/common/post-detail";
    }

    @GetMapping("/posts/edit")
    public String showPostsEditPage(@RequestParam("postId") Long postId,@RequestParam("boardId") String boardId ,Model model) {

        Optional<Posts> postsData = postsRepository.findByBoard_BoardIdAndPostId(boardId, postId);

        if (postsData.isPresent()) {
            model.addAttribute("postData", postsData.get());
        }

        return "/board/common/post-edit-form";
    }


}
