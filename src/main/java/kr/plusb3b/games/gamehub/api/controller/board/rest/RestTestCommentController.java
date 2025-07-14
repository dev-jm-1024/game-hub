package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.dto.RequestCommentDto;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateCommentsVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
//@RequestMapping("/api/v1")
public class RestTestCommentController {

//    private final AccessControlService access;
//    private final CommentService commentService;
//
//    public RestTestCommentController(AccessControlService access, CommentService commentService) {
//        this.access = access;
//        this.commentService = commentService;
//    }
//
//    @PostMapping("/posts/{postId}/comments")
//    public ResponseEntity<?> submitComment(@RequestBody @Validated RequestCommentDto requestCommentDto,
//                                           HttpServletRequest request) {
//        try {
//            // 1. 로그인 사용자 확인
//            User user = access.getAuthenticatedUser(request);
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body("로그인이 필요합니다.");
//            }
//
//            // 2. 댓글 기본 VO는 서비스 내부에서 처리해도 무방
//            boolean result = commentService.createComment(new CreateCommentsVO(), requestCommentDto, user);
//            if (result) {
//                return ResponseEntity.status(HttpStatus.CREATED)
//                        .body("댓글이 성공적으로 등록되었습니다.");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("댓글 등록에 실패했습니다.");
//            }
//
//        } catch (DataAccessException dae) {
//            dae.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("데이터베이스 오류가 발생했습니다.");
//        } catch (IllegalArgumentException iae) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(iae.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("알 수 없는 오류가 발생했습니다.");
//        }
//    }
}
