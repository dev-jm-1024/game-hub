package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path="/api/v1/board/posts")
public class RestPostsInteractionController {

    private final PostsInteractionService postInteract;
    private final AccessControlService access;
    private final PostsRepository postsRepo;

    public RestPostsInteractionController(PostsInteractionService postInteract,
                                          AccessControlService access,
                                          PostsRepository postsRepo) {
        this.postInteract = postInteract;
        this.access = access;
        this.postsRepo = postsRepo;
    }

    // 좋아요 등록: POST /api/v1/board/posts/{postId}/reactions/likes
    @PostMapping("/{postId}/reactions/likes")
    public ResponseEntity<?> createPostsLike(@PathVariable("postId") Long postId,
                                        HttpServletRequest request, HttpServletResponse response) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다");
        }


        // 2. 좋아요 등록 처리
        try {
            boolean result = postInteract.likePost(user, postId);

            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("좋아요 등록 성공");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("이미 반응이 등록되어 있습니다");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }

    // 좋아요 취소: DELETE /api/v1/board/posts/{postId}/reactions/likes
    @DeleteMapping("/{postId}/reactions/likes")
    public ResponseEntity<?> deleteLike(@PathVariable("postId") Long postId,
                                        HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다");
        }


        // 3. 좋아요 취소 처리
        try {
            boolean result = postInteract.likePostCancel(user, postId);

            if (result) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("좋아요 취소 성공");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("취소할 좋아요가 없습니다");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }

    // 싫어요 등록: POST /api/v1/board/posts/{postId}/reactions/dislikes
    @PostMapping("/{postId}/reactions/dislikes")
    public ResponseEntity<?> createDislike(@PathVariable("postId") Long postId,
                                           HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다");
        }


        // 2. 싫어요 등록 처리
        try {
            boolean result = postInteract.dislikePost(user, postId);

            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("싫어요 등록 성공");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("이미 반응이 등록되어 있습니다");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }

    // 싫어요 취소: DELETE /api/v1/board/posts/{postId}/reactions/dislikes
    @DeleteMapping("/{postId}/reactions/dislikes")
    public ResponseEntity<?> deleteDislike(@PathVariable("postId") Long postId,
                                           HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다");
        }


        // 2. 싫어요 취소 처리
        try {
            boolean result = postInteract.dislikePostCancel(user, postId);

            if (result) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("싫어요 취소 성공");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("취소할 싫어요가 없습니다");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }

    // 신고 등록: PATCH /api/v1/board/posts/{postId}/reactions/report
    @PatchMapping("/{postId}/reactions/report")
    public ResponseEntity<?> reportPost(@PathVariable("postId") Long postId,
                                        HttpServletRequest request) {

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");


        // 2. 신고 처리
        try {
            boolean result = postInteract.reportPost(user, postId);

            if (result) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("신고가 접수되었습니다");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("이미 신고한 게시물입니다");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }

    // 게시물 반응 조회: GET /api/v1/board/posts/{postId}/reactions
    @GetMapping("/{postId}/reactions")
    public ResponseEntity<?> getPostReactions(@PathVariable("postId") Long postId) {
        try {
            PostsReactionCount reactionCount = postInteract.getPostsReactionCount(postId);

            if (reactionCount == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("게시물이 존재하지 않습니다");
            }

            // Map 으로 변환하여 순환참조 방지
            Map<String, Integer> response = Map.of(
                    "likeCount", reactionCount.getLikeCount(),
                    "dislikeCount", reactionCount.getDislikeCount(),
                    "reportCount", reactionCount.getReportCount()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다");
        }
    }
}