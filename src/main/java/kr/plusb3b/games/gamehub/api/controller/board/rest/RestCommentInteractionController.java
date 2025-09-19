package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/board/posts/comments")
public class RestCommentInteractionController {

    private final CommentsInteractionService commentsInteract;
    private final AccessControlService access;

    public RestCommentInteractionController(CommentsInteractionService commentsInteract, AccessControlService access) {
        this.commentsInteract = commentsInteract;
        this.access = access;
    }

    // 좋아요 등록: POST /api/v1/board/posts/comments/{commentId}/reactions/likes
    @PostMapping("/{commentId}/reactions/likes")
    public ResponseEntity<?> createLike(@PathVariable("commentId") Long commentId,
                                        HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다");
        }

        boolean result = commentsInteract.likeComment(user, commentId);

        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("좋아요 등록 성공");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 반응이 등록되어 있습니다");
        }

    }

    // 좋아요 취소: DELETE /api/v1/board/posts/comments/{commentId}/reactions/likes
    @DeleteMapping("/{commentId}/reactions/likes")
    public ResponseEntity<?> deleteLike(@PathVariable("commentId") Long commentId,
                                        HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다");
        }

        boolean result = commentsInteract.likeCommentCancel(user, commentId);

        if (result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("좋아요 취소 성공");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("취소할 좋아요가 없습니다");
        }
    }

    // 싫어요 등록: POST /api/v1/board/posts/comments/{commentId}/reactions/dislikes
    @PostMapping("/{commentId}/reactions/dislikes")
    public ResponseEntity<?> createDislike(@PathVariable("commentId") Long commentId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        access.validateUserAccess(request, response);
        boolean result = commentsInteract.dislikeComment(user, commentId);

        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("싫어요 등록 성공");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 반응이 등록되어 있습니다");
        }
    }

    // 싫어요 취소: DELETE /api/v1/board/posts/comments/{commentId}/reactions/dislikes
    @DeleteMapping("/{commentId}/reactions/dislikes")
    public ResponseEntity<?> deleteDislike(@PathVariable("commentId") Long commentId,
                                           HttpServletRequest request,
                                                HttpServletResponse response) throws IOException{

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        access.validateUserAccess(request, response);

        boolean result = commentsInteract.dislikeCommentCancel(user, commentId);

        if (result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("싫어요 취소 성공");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("취소할 싫어요가 없습니다");
        }

    }

    // 댓글 반응 조회: GET /api/v1/board/posts/comments/{commentId}/reactions
    @GetMapping("/{commentId}/reactions")
    public ResponseEntity<?> getCommentReactions(@PathVariable("commentId") Long commentId) {

        CommentsReactionCount reactionCount = commentsInteract.getCommentsReactionCount(commentId);

        if (reactionCount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("댓글이 존재하지 않습니다");
        }

        return ResponseEntity.ok(reactionCount);
    }

    // 댓글 신고: PATCH /api/v1/board/posts/comments/{commentId}/reactions/report
    @PatchMapping("/{commentId}/reactions/report")
    public ResponseEntity<?> reportComment(@PathVariable("commentId") Long commentId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        access.validateUserAccess(request, response);

        boolean result = commentsInteract.reportComment(user, commentId);

        if (result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("댓글 신고가 접수되었습니다");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 신고한 댓글입니다");
        }
    }
}