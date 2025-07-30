package kr.plusb3b.games.gamehub.api.controller.board;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.PostsNotFoundException;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostFilesRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.service.UserInteractionProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/board")
public class PostsController {

    private final PostFilesRepository postFilesRepo;
    private final PostsService postsService;
    private final AccessControlService access;
    private final CommentService commentService;
    private final UserInteractionProvider userInteractionProvider;
    private final PostsInteractionService postsInteractionService;

    public PostsController(PostFilesRepository postFilesRepo, PostsService postsService,
                           AccessControlService access, CommentService commentService,
                           UserInteractionProvider userInteractionProvider,
                           PostsInteractionService postsInteractionService) {

        this.postFilesRepo = postFilesRepo;
        this.postsService = postsService;
        this.access = access;
        this.commentService = commentService;
        this.userInteractionProvider = userInteractionProvider;
        this.postsInteractionService = postsInteractionService;
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

        if(posts==null){
            return "redirect:/board";
        }

        Optional<PostFiles> postFilesOpt = postFilesRepo.findPostFilesByPost_PostId(postId);
        postFilesOpt.ifPresent(postFiles -> model.addAttribute("postFiles", postFiles));
        model.addAttribute("hasPostFile", postFilesOpt.isPresent());

        //해당 게시물의 댓글 가져오기

        List<Comments> commentsList = commentService.getComments(boardId, postId);

        try{

            User user = access.getAuthenticatedUser(request);
            if(user == null) throw new IllegalArgumentException("사용자를 찾을 수 없습니다");

            boolean isAuthor = postsService.isAuthor(request, postId);
            System.out.println("isAuthor Result: "+isAuthor); //25.07.17 17:04 기준 true 나옴

            /********************로그인한 회원이 게시물 쓴건지 확인************************/
            if(isAuthor) model.addAttribute("isAuthUser", true);
            else model.addAttribute("isAuthUser", false);


            /**********************View 에다가 데이터 전송**********************/

            //1. 게시물 데이터
            model.addAttribute("postsData", posts);

            //2. 사용자의 좋아요나 싫어요 같은 거 눌렀는 지 여부
            UserPostsReaction.ReactionType reactType =
                userInteractionProvider.getUserReactionType(posts, user);

            model.addAttribute("reactType", reactType);

            //3. 게시물의 interaction count 데이터
            PostsReactionCount postsReactionCount =
                postsInteractionService.getPostsReactionCount(postId);

            model.addAttribute("postsReactionCount", postsReactionCount);


            //4. 댓글 데이터
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

    // GlobalExceptionHandler
    @ExceptionHandler(PostsNotFoundException.class)
    public String handlePostNotFound(PostsNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "해당 게시글이 존재하지 않습니다.");
        model.addAttribute("errorCode", "ERR-POST-404");
        return "error/post-not-found"; // 사용자 친화적인 에러 페이지
    }
}
