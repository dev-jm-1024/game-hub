package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.HttpServletRequest;
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
    private final BoardServiceImpl boardServiceImpl;
    private final PostsServiceImpl postsServiceImpl;
    private final AccessControlService access;
    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BoardController.class);4

//    @Value("${app.max.boardSize}")
//    String maxBoardSizeString;
//    int maxBoardSize = Integer.parseInt(maxBoardSizeString);


    public BoardController(PostFilesRepository postFilesRepo, BoardServiceImpl boardServiceImpl,
                           PostsServiceImpl postsServiceImpl, AccessControlService access) {
        this.postFilesRepo = postFilesRepo;
        this.boardServiceImpl = boardServiceImpl;
        this.postsServiceImpl = postsServiceImpl;
        this.access = access;
    }

    @GetMapping
    public String boardMainPage(Model model) {
        // 게시판별로 게시물 분류 및 필터링/정렬
        Map<String, List<Posts>> postsByBoard = boardServiceImpl.loadTop5LatestPostsByBoard();

        // View에 전달
        model.addAttribute("postsByBoard", postsByBoard);

        return "board/main-board"; // 템플릿 경로
    }



    //게시판 경로 처리
    @GetMapping("/{boardId}/view")
    public String dispatchBoardPost(@PathVariable("boardId") String boardId, Model model) {

        List<SummaryPostDto> summaryPosts = postsServiceImpl.summaryPosts(boardId);

        //postAct 1인 데이터 Model로 넘기기
        model.addAttribute("summaryPosts", summaryPosts);

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
                                    @PathVariable("postId") Long postId, Model model,
                                    HttpServletRequest request){


        Posts posts = postsServiceImpl.detailPosts(boardId, postId);
        PostFiles postFiles = postFilesRepo.findPostFilesByPost_PostId(postId)
                        .orElseThrow(() -> new PostsNotFoundException(postId));

        try{

            User user = access.getAuthenticatedUser(request);
            if(user == null) throw new IllegalArgumentException("사용자를 찾을 수 없습니다");

            boolean isAuthor = postsServiceImpl.isAuthor(request, posts);

            /********************로그인한 회원이 게시물 쓴건지 확인************************/
            if(!isAuthor) model.addAttribute("isAuthUser", true);
            else model.addAttribute("isAuthUser", false);


            //View 에다가 데이터 전송
            model.addAttribute("postsData", posts);
            model.addAttribute("postFiles", postFiles);


        }catch (AuthenticationCredentialsNotFoundException e) {
            e.printStackTrace();
        }


        return "board/common/post-detail";
    }

    @GetMapping("/posts/edit")
    public String showPostsEditPage(@RequestParam("postId") Long postId,
                                    @RequestParam("boardId") String boardId,
                                    Model model) {

        Posts postData = postsServiceImpl.detailPosts(boardId, postId); // 수정: 단일 Posts 리턴
        model.addAttribute("postData", postData);

        return "/board/common/post-edit-form";
    }


}