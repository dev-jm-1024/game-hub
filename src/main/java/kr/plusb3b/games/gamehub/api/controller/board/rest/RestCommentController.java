package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.dto.RequestCommentDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
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
    private final PostsRepository postsRepo;

    public RestCommentController(AccessControlService access, CommentService commentService,
                                 PostsRepository postsRepo) {
        this.access = access;
        this.commentService = commentService;
        this.postsRepo = postsRepo;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> submitComment(@ModelAttribute @Validated RequestCommentDto requestCommentDto,
                                           @PathVariable Long postId,
                                           HttpServletRequest request) {

        // PathVariable의 postId와 RequestBody의 postId 일치 확인
        if (!postId.equals(requestCommentDto.getPostId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("경로의 postId와 요청 본문의 postId가 일치하지 않습니다.");
        }

        try {
            // 1. 로그인 사용자 확인
            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            // 2. 댓글 기본 VO는 서비스 내부에서 처리해도 무방
            boolean result = commentService.createComment(new CreateCommentsVO(), requestCommentDto, user);
            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("댓글이 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("댓글 등록에 실패했습니다.");
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
                    .body("알 수 없는 오류가 발생했습니다.");
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


    //싫어요, 좋아요 증가 로직

}
