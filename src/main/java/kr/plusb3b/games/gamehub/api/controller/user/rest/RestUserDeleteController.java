package kr.plusb3b.games.gamehub.api.controller.user.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserDeleteService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/game-hub/deactivate")
public class RestUserDeleteController {

    private final UserAuthRepository userAuthRepo;
    private final AccessControlService accessControlService;
    private final UserDeleteService userDeleteService;

    public RestUserDeleteController(UserAuthRepository userAuthRepo,
                                    AccessControlService accessControlService,
                                    UserDeleteService userDeleteService) {
        this.userAuthRepo = userAuthRepo;
        this.accessControlService = accessControlService;
        this.userDeleteService = userDeleteService;
    }

    @PostMapping
    public ResponseEntity<?> deactivateUser(@RequestParam("authUserId") String authUserId,
                                            @RequestParam("authUserPassword") String authUserPassword,
                                            HttpServletRequest request) {

        // 1. 필수 입력값 확인
        if (!StringUtils.hasText(authUserId) || !StringUtils.hasText(authUserPassword)) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 누락되었습니다.");
        }

        // 2. 아이디 유효성 확인
        Optional<UserAuth> userAuthOpt = userAuthRepo.findByAuthUserId(authUserId);
        if (userAuthOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("존재하지 않는 아이디입니다.");
        }

        // 3. 로그인된 사용자 확인
        User currentUser = accessControlService.getAuthenticatedUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        try {
            // 4. 비밀번호 검증 + 탈퇴 처리
            boolean success = userDeleteService.deactivateUser(currentUser.getMbId(), authUserPassword);
            if (success) {
                return ResponseEntity.ok("회원 탈퇴가 완료되었습니다. 30일간 정보가 보관된 후 완전 삭제됩니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("회원 탈퇴 처리 중 오류가 발생했습니다.");
            }

        } catch (IllegalArgumentException e) {
            // 비밀번호 불일치 또는 기타 도메인 예외
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
