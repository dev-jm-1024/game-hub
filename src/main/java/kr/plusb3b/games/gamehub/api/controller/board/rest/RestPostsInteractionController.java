package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.application.board.PostsInteractionServiceImpl;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1/board")
public class RestPostsInteractionController {

    private final PostsInteractionService postInteract;
    private final AccessControlService access;

    public RestPostsInteractionController(PostsInteractionService postInteract, AccessControlService access) {
        this.postInteract = postInteract;
        this.access = access;
    }

    //posts like path : /posts/{postId}/like
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePostsByPostId(@PathVariable("postId") Long postId, HttpServletRequest request){

        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");

        //게시물 좋아요 저장 + 사용자 좋아요 기록 저장
        boolean result = postInteract.likePost(postId, user.getUserAuth().getAuthUserId(), request);
        if(result) return ResponseEntity.status(HttpStatus.OK).body("like success");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("like fail");

    }

    //posts like cancel path : /posts/{postId}/like
    @PatchMapping("/posts/{postId}/like")
    public ResponseEntity<?> cancelLikePostsByPostId(@PathVariable("postId") Long postId, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body("like cancel success");
    }

    //posts dislike path : /posts/{postId}/dislike

    //posts dislike cancel path : /posts/{postId}/dislike

    //posts report path : /posts/{postId}/report

    //posts report cancel path : /posts/{postId}/report

}
