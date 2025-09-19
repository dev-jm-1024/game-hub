package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.admin.AdminPostsService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminPostsController {

    private final AccessControlService access;
    private final PostsService postsService;
    private final AdminPostsService adminPostsService;

    public RestAdminPostsController(AccessControlService access, PostsService postsService,
                                    AdminPostsService adminPostsService) {
        this.access = access;
        this.postsService = postsService;
        this.adminPostsService = adminPostsService;
    }


    @PostMapping("/posts/{postId}/deactivate")
    public ResponseEntity<?> deactivatePost(@PathVariable("postId") Long postId, HttpServletRequest request){

        User user = access.getAuthenticatedUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다");
        else if(user.getMbRole() != User.Role.ROLE_ADMIN) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 존재하지 않습니다");

        // postId 유효성 검증 추가
        if(postId == null || postId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 게시물 ID입니다");
        }

        try {
            boolean result = adminPostsService.deactivatePost(postId);
            if(!result) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제에 실패했습니다");

            return ResponseEntity.status(HttpStatus.OK).body("삭제에 성공하였습니다");
        } catch (Exception e) {
            // 로그 추가 권장
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다");
        }
    }

}
