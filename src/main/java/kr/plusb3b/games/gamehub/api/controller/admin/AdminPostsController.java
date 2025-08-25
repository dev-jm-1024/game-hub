package kr.plusb3b.games.gamehub.api.controller.admin;

import jakarta.servlet.http.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/board")
public class AdminPostsController {

    private final PostsService postsService;
    private final BoardService boardService;
    private final PostFilesService postFilesService;
    private final AccessControlService access;

    private final String REDIRECT_PATH = "redirect:/game-hub";
    private final String NOTICE_PATH = "notice";

    public AdminPostsController(PostsService postsService,
                                BoardService boardService, PostFilesService postFilesService,
                                AccessControlService access) {
        this.postsService = postsService;
        this.boardService = boardService;
        this.postFilesService = postFilesService;
        this.access = access;
    }

    //공지사항은 별도로 처리 - 읽기/쓰기 이슈
    @GetMapping("/posts-status")
    public String showPostsStatusPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);

        //전체 게시물 데이터
        List<Posts> postList = postsService.getAllPosts().stream()
                .filter(
                        p -> !p.getBoard().getBoardId().equals(NOTICE_PATH)
                )
                .collect(Collectors.toList());

        model.addAttribute("postList", postList);


        return "admin/post-status/index";
    }

    //GET: /admin/board/{boardId}/post-status
    @GetMapping("/{boardId}/post-status")
    public String viewPostStatusPage(Model model, @PathVariable("boardId") String boardId){


        List<Posts> postsList = postsService.getAllPosts().stream()
                .filter(p -> p.getBoard().activateBoard())
                .filter(p -> p.getBoard().getBoardId().equals(boardId))
                .collect(Collectors.toList());

        model.addAttribute("postsList", postsList);

        Optional<Board> boardOpt = boardService.getAllBoards().stream()
                .filter(b -> b.getBoardId().equals(boardId))
                .findFirst();

        model.addAttribute("board", boardOpt.orElse(null));

        return "admin/post-status/check-posts";

    }

    @GetMapping("/{boardId}/post-status/{postId}/detail")
    public String viewPostDetailPage(Model model, @PathVariable("boardId") String boardId,
                                     @PathVariable("postId") Long postId){


       Posts posts = postsService.detailPosts(boardId, postId);
        model.addAttribute("posts", posts);


        List<PostFiles> files = postFilesService.getPostFiles(postId);
        model.addAttribute("files", files);

        return "admin/post-status/check-posts-detail";
    }



}
