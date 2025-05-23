package kr.plusb3b.games.gamehub.api.controller.board;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.board.PostsNotFoundException;
import kr.plusb3b.games.gamehub.api.dto.board.TestDataBoard;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.atmosphere.config.service.Get;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostsRepository postsRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);


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

            boolean hasPosts = postsRepository.existsPostsByBoard_BoardId(boardId);
            hasPostsMap.put(boardId, hasPosts);

            if (hasPosts) {
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



    //자유 게시판 경로 처리
    @GetMapping("/free")
    public String showFreeBoardPage(Model model){

        //model.addAttribute("boardList", boardRepository.findAll());
        //게시판에 보여져야할 것 들: Number, title, author, viewCount, createdAt

        List<Posts> postsList = postsRepository.findAll();
        boolean isPostsListEmpty = postsList.isEmpty();
        model.addAttribute("isPostsListEmpty", isPostsListEmpty);

        if(isPostsListEmpty){
            model.addAttribute("postsList", postsList);
        }
        
        return "board/comon/post-list";
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
    public String showPostPage(@PathVariable("boardId") String boardId){

        return "board/common/post-form";
    }


    //나머지 메소드들이 완성되면 추후에 합침
//    public String dispatchBoardPost(Model model){
//
//        return "test";
//    }

    //게시물 데이터 가져오기
    @GetMapping("/{boardId}/{postId}/view")
    public String showPostDetailPage(@PathVariable("boardId") String boardId,
                                     @PathVariable("postId") Long postId,
                                     Model model) {

        Posts post = postsRepository.findByBoard_BoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        model.addAttribute("postsData", post);
        return "board/common/post-detail";
    }

    // GlobalExceptionHandler
    @ExceptionHandler(PostsNotFoundException.class)
    public String handlePostNotFound(PostsNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "해당 게시글이 존재하지 않습니다.");
        model.addAttribute("errorCode", "ERR-POST-404");
        return "error/post-not-found"; // 사용자 친화적인 에러 페이지
    }


    // /boards/{boardId}/{postsId}/view
    @GetMapping("/{boardId}/{postsId}/view")
    public String showPostsViewPage(@PathVariable("boardId") String boardId,
                                    @PathVariable("postsId") Long postsId,Model model){

        //게시물이 보여서 링크타고 들어오면 당연히 게시물 데이터가 존재함.
        //근데 만약 없는 경우? 이건 내부적으로 예외처리를 해야한다.
        Posts posts = postsRepository.findByBoard_BoardIdAndPostId(boardId, postsId)
                .orElseThrow(() -> new PostsNotFoundException(postsId));

        //View 에다가 데이터 전송
        model.addAttribute("postsData", posts);
        return "/board/common/post-detail";
    }


}
