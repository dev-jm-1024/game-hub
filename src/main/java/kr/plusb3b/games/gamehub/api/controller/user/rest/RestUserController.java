package kr.plusb3b.games.gamehub.api.controller.user.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.dto.ChangePasswordDto;
import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.service.UserDeleteService;
import kr.plusb3b.games.gamehub.domain.user.service.UserUpdateService;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.reflections.Reflections.log;

@RestController
@RequestMapping("/api/v1")
public class RestUserController {

    private final AccessControlService access;
    private final UserUpdateService userUpdateService;
    private final UserAuthRepository userAuthRepo;
    private final UserDeleteService userDeleteService;

    public RestUserController(AccessControlService access, UserUpdateService userUpdateService,
                              UserAuthRepository userAuthRepo, UserDeleteService userDeleteService) {
        this.access = access;
        this.userUpdateService = userUpdateService;
        this.userAuthRepo = userAuthRepo;
        this.userDeleteService = userDeleteService;
    }

    @PatchMapping("/user/{mbId}")
    public ResponseEntity<?> updateUser(@RequestBody RequestUserUpdateDto updateDto,
                                        @PathVariable("mbId") Long mbId,
                                        HttpServletRequest request) {
        try {
            User authUser = access.getAuthenticatedUser(request);
            boolean realMbId = authUser.getMbId().equals(mbId);

            if (!realMbId) {
                log.warn("사용자 권한 없음 - 요청 mbId: {}, 로그인 사용자 ID: {}", mbId, authUser.getMbId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            userUpdateService.updateUserProfile(mbId, updateDto);
            log.info("사용자 정보 업데이트 성공 - 사용자 ID: {}", mbId);
            return ResponseEntity.ok("사용자 정보가 업데이트되었습니다.");

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            log.error("서버 오류로 인해 사용자 정보 업데이트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보 업데이트 중 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/user/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                            HttpServletRequest request) {
        try {
            User user = access.getAuthenticatedUser(request);
            log.info("비밀번호 변경 요청 - 사용자 ID: {}", user.getMbId());

            userUpdateService.updateUserPassword(
                    user.getMbId(),
                    changePasswordDto.getOldPassword(),
                    changePasswordDto.getNewPassword()
            );

            log.info("비밀번호 변경 성공 - 사용자 ID: {}", user.getMbId());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");

        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            log.error("서버 오류로 인해 비밀번호 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }


    @PatchMapping("/user/{mbId}/deactivate")
    public ResponseEntity<?> deactivateUserStatus(@RequestParam("authUserId")String authUserId,
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
        User currentUser = access.getAuthenticatedUser(request);
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
