package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.PostsNotFoundException;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostFilesRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/board")
public class PostsController {

    private final PostFilesRepository postFilesRepo;
    private final BoardService boardService;
    private final PostsService postsService;
    private final AccessControlService access;
    private final BoardRepository boardRepo;
    private final CommentService commentService;

    public PostsController(PostFilesRepository postFilesRepo, BoardService boardService,
                           PostsService postsService, AccessControlService access, BoardRepository boardRepo,
                           CommentService commentService) {
        this.postFilesRepo = postFilesRepo;
        this.boardService = boardService;
        this.postsService = postsService;
        this.access = access;
        this.boardRepo = boardRepo;
        this.commentService = commentService;
    }

        //글 작성 페이지 경로 처리
    @GetMapping("/new")
    public String showPostPage(@RequestParam("boardId") String boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "board/common/post-form";
    }

    // /boards/{boardId}/{postsId}/view
    @GetMapping("/{boardId}/{postId}/view")
    public String showPostsViewPage(@PathVariable("boardId") String boardId,
                                    @PathVariable("postId") Long postId, Model model,
                                    HttpServletRequest request){


        Posts posts = postsService.detailPosts(boardId, postId);
        PostFiles postFiles = postFilesRepo.findPostFilesByPost_PostId(postId)
                .orElseThrow(() -> new PostsNotFoundException(postId));

        //해당 게시물의 댓글 가져오기

        List<Comments> commentsList = commentService.getComments(boardId, postId);

        try{

            User user = access.getAuthenticatedUser(request);
            if(user == null) throw new IllegalArgumentException("사용자를 찾을 수 없습니다");

            boolean isAuthor = postsService.isAuthor(request, posts);

            /********************로그인한 회원이 게시물 쓴건지 확인************************/
            if(!isAuthor) model.addAttribute("isAuthUser", true);
            else model.addAttribute("isAuthUser", false);


            //View 에다가 데이터 전송

            //게시물 데이터
            model.addAttribute("postsData", posts);

            //첨부파일 데이터
            model.addAttribute("postFiles", postFiles);

            //댓글 데이터
            model.addAttribute("commentsList", commentsList);


        }catch (AuthenticationCredentialsNotFoundException e) {
            e.printStackTrace();
        }


        return "board/common/post-detail";
    }

    @GetMapping("/posts/edit")
    public String showPostsEditPage(@RequestParam("postId") Long postId,
                                    @RequestParam("boardId") String boardId,
                                    Model model) {

        Posts postData = postsService.detailPosts(boardId, postId); // 수정: 단일 Posts 리턴
        model.addAttribute("postData", postData);

        return "/board/common/post-edit-form";
    }

}
