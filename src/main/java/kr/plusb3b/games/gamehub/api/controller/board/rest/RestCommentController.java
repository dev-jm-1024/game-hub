package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.dto.RequestCommentDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class RestCommentController {

    private final AccessControlService access;
    private final CommentsRepository commentsRepo;
    private final PostsRepository postsRepo;

    public RestCommentController(AccessControlService access, CommentsRepository commentsRepo,
                                 PostsRepository postsRepo) {
        this.access = access;
        this.commentsRepo = commentsRepo;
        this.postsRepo = postsRepo;
    }

    @PostMapping("/posts/{postsId}/comments")
    public ResponseEntity<?> submitComment(@RequestBody RequestCommentDto requestCommentDto, HttpServletRequest request) {

        try {
            // 로그인한 사용자 검증
            User user = access.getAuthenticatedUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 게시글 존재 여부 확인
            Optional<Posts> postsOptional = postsRepo.findById(requestCommentDto.getPostId());
            if (postsOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글을 찾을 수 없습니다.");
            }

            String commentContent = requestCommentDto.getCommentContent();
            if (commentContent == null || commentContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("댓글 내용을 입력해주세요.");
            }

            // 댓글 객체 생성 및 조립
            Comments comments = new Comments();
            comments.setPosts(postsOptional.get());
            comments.setUser(user);
            comments.setCommentContent(commentContent);
            comments.setLikeCount(0);
            comments.setDislikeCount(0);
            comments.setReportCount(0);

            // DB 저장
            Comments savedComment = commentsRepo.save(comments);
            return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 성공적으로 등록되었습니다.");

        } catch (DataAccessException dae) {
            dae.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류가 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }

}
