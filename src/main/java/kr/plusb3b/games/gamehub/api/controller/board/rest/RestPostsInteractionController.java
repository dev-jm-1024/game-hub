package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.application.board.PostsInteractionServiceImpl;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path="/api/v1/board")
public class RestPostsInteractionController {

    private final PostsInteractionService postInteract;
    private final AccessControlService access;
    private final UserPostsReactionRepository userReactRepo;
    private final PostsRepository postsRepo;

    public RestPostsInteractionController(PostsInteractionService postInteract, AccessControlService access,
                                          UserPostsReactionRepository userReactRepo, PostsRepository postsRepo) {
        this.postInteract = postInteract;
        this.access = access;
        this.userReactRepo = userReactRepo;
        this.postsRepo = postsRepo;
    }

    //posts like path : /posts/{postId}/like
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePostsByPostId(@PathVariable("postId") Long postId, HttpServletRequest request){

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        }

        // 2. 게시물 존재 확인
        Optional<Posts> postsOpt = postsRepo.findById(postId);
        if(postsOpt.isEmpty()) { // null 체크가 아닌 isEmpty() 사용
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 존재하지 않습니다");
        }

        // 3. 게시물 좋아요 처리
        try {
            boolean result = postInteract.likePost(postId, user.getUserAuth().getAuthUserId(), request);

            if(result) {
                return ResponseEntity.status(HttpStatus.OK).body("좋아요 처리 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 처리 실패");
            }

        } catch (Exception e) {
            // 예외 처리 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }

    //posts like cancel path : /posts/{postId}/like
    @PatchMapping("/posts/{postId}/like")
    public ResponseEntity<?> cancelLikePostsByPostId(@PathVariable("postId") Long postId, HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        }

        // 2. 게시물 존재 확인
        Optional<Posts> postsOpt = postsRepo.findById(postId);
        if (postsOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 존재하지 않습니다");
        }

        // 3. 좋아요 취소 처리
        try {
            boolean result = postInteract.likePostCancel(postId, user.getUserAuth().getAuthUserId(), request);

            if (result) {
                return ResponseEntity.status(HttpStatus.OK).body("좋아요 취소 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 취소 실패 (좋아요 기록이 없습니다)");
            }

        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }
    //posts dislike path : /posts/{postId}/dislike

    //posts dislike cancel path : /posts/{postId}/dislike

    //posts report path : /posts/{postId}/report

    //posts report cancel path : /posts/{postId}/report

}
