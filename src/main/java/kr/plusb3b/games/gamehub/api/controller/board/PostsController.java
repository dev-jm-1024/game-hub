package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.PostsNotFoundException;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostDetailVmService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostEditFormVmService;
import kr.plusb3b.games.gamehub.view.board.PostDetailVM;
import kr.plusb3b.games.gamehub.view.board.PostEditFormVM;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
public class PostsController {

    /**리팩토링 후 의존성**/
    private final PostDetailVmService postDetailVmService;
    private final PostEditFormVmService postEditFormVmService;
    private final BoardService boardService;

    public PostsController(PostDetailVmService postDetailVmService, PostEditFormVmService postEditFormVmService,
                           BoardService boardService) {
        this.postDetailVmService = postDetailVmService;
        this.postEditFormVmService = postEditFormVmService;
        this.boardService = boardService;
    }

    //글 작성 페이지 경로 처리
    @GetMapping("/{boardId}/new")
    public String viewPostForm(@PathVariable("boardId") String boardId, Model model) {
        List<Board> vm = boardService.getAllBoards();

        model.addAttribute("boardId", boardId);
        model.addAttribute("vm", vm);
        return "board/common/post-form";
    }



    // /boards/{boardId}/{postsId}/view
    @GetMapping("/{boardId}/{postId}/view")
    public String viewPostDetail(@PathVariable("boardId") String boardId,
                                 @PathVariable("postId") Long postId,
                                 HttpServletRequest request, Model model) {

        try {
            PostDetailVM viewModel = postDetailVmService.getPostDetailVm(boardId, postId, request);
            model.addAttribute("postDetail", viewModel);
            return "board/common/post-detail";
        } catch (IllegalArgumentException e) {
            return "redirect:/board";
        }
    }

    @GetMapping("/posts/edit")
    public String viewPostEditForm(@RequestParam("postId") Long postId,
                                    @RequestParam("boardId") String boardId,
                                    HttpServletRequest request,
                                    Model model) {

        PostEditFormVM vm = postEditFormVmService.getPostEditFormVm(boardId, postId, request);
        model.addAttribute("vm", vm);

        return "/board/common/post-edit-form";
    }

    // GlobalExceptionHandler
    @ExceptionHandler(PostsNotFoundException.class)
    public String handlePostNotFound(PostsNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "해당 게시글이 존재하지 않습니다.");
        model.addAttribute("errorCode", "ERR-POST-404");
        return "error/post-not-found"; // 사용자 친화적인 에러 페이지
    }
}
