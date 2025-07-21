package kr.plusb3b.games.gamehub.api.controller.user.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.dto.ChangePasswordDto;
import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserUpdateService;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class RestUserTestController {

    private final AccessControlService access;
    private final UserUpdateService userUpdateService;

    public RestUserTestController(AccessControlService access,
                                  UserUpdateService userUpdateService) {
        this.access = access;
        this.userUpdateService = userUpdateService;
    }

    @PostMapping("/user/password")
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
}
