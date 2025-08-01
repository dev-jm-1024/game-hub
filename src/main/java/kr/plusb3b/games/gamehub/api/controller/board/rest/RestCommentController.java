package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.RequestCommentDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.vo.CommentsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateCommentsVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class RestCommentController {

    private final AccessControlService access;
    private final CommentService commentService;
    private final CommentsInteractionService commentsInteractionService;

    public RestCommentController(AccessControlService access, CommentService commentService,
                                 CommentsInteractionService commentsInteractionService) {
        this.access = access;
        this.commentService = commentService;
        this.commentsInteractionService = commentsInteractionService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> submitComment(@ModelAttribute @Validated RequestCommentDto requestCommentDto,
                                           @PathVariable Long postId,
                                           HttpServletRequest request) {

        // 1. PathVariable과 요청 DTO 간 postId 일치 여부 확인
        if (!postId.equals(requestCommentDto.getPostId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("경로의 postId와 요청 본문의 postId가 일치하지 않습니다.");
        }

        try {
            // 2. 로그인한 사용자 확인
            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 3. 댓글 생성
            Comments savedComment = commentService.createComment(new CreateCommentsVO(), requestCommentDto, user);

            // 4. 댓글 반응 카운트 초기화
            boolean reactionInitResult = commentsInteractionService
                    .saveCommentsReactionCount(savedComment.getCommentId(), new CommentsReactionCountVO());

            if (savedComment != null && reactionInitResult) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("댓글이 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("댓글 등록 또는 반응 초기화에 실패했습니다.");
            }

        } catch (DataAccessException dae) {
            dae.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터베이스 오류가 발생했습니다.");
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(iae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("알 수 없는 서버 오류가 발생했습니다.");
        }
    }


    //댓글 내용 업데이트
    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody String content,
                                           HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body("로그인 상태가 아닙니다! 로그인 진행해주세요!");


        boolean result = commentService.updateCommentContents(commentId, content);

        if(!result) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("알 수 없는 오류가 발생했습니다.");

        return ResponseEntity.status(HttpStatus.OK).body("댓글 수정에 성공하였습니다");
    }

}
