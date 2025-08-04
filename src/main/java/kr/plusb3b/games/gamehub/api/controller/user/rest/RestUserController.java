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
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.reflections.Reflections.log;

@RestController
@RequestMapping("/api/v1")
public class RestUserController {

    private final AccessControlService access;
    private final UserUpdateService userUpdateService;
    private final UserAuthRepository userAuthRepo;
    private final UserDeleteService userDeleteService;
    private final FileUpload fileUpload;

    public RestUserController(AccessControlService access, UserUpdateService userUpdateService,
                              UserAuthRepository userAuthRepo, UserDeleteService userDeleteService,
                              FileUpload fileUpload) {
        this.access = access;
        this.userUpdateService = userUpdateService;
        this.userAuthRepo = userAuthRepo;
        this.userDeleteService = userDeleteService;
        this.fileUpload = fileUpload;
    }

    // RestUserController.java에 추가할 메서드

    @PostMapping("/user/{mbId}/profile-image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable("mbId") Long mbId,
                                                @RequestPart("file") MultipartFile file,
                                                HttpServletRequest request) {
        try {
            // 1. 인증된 사용자 확인
            User authUser = access.getAuthenticatedUser(request);
            if (!authUser.getMbId().equals(mbId)) {
                log.warn("프로필 이미지 업로드 권한 없음 - 요청 mbId: {}, 로그인 사용자 ID: {}", mbId, authUser.getMbId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            // 2. 파일 유효성 검사
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업로드할 파일이 없습니다.");
            }

            // 이미지 파일 검증
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 파일만 업로드 가능합니다.");
            }

            // 파일 크기 제한 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 크기는 5MB 이하로 업로드해주세요.");
            }

            // 3. 프로필 이미지 업로드 처리
            String profileImageUrl = userUpdateService.uploadAndUpdateProfileImage(mbId, file);

            // 4. 성공 응답
            Map<String, String> response = new HashMap<>();
            response.put("profileUrl", profileImageUrl);
            response.put("message", "프로필 이미지가 성공적으로 업데이트되었습니다.");

            log.info("프로필 이미지 업로드 성공 - 사용자 ID: {}, URL: {}", mbId, profileImageUrl);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("프로필 이미지 업로드 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 중 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("프로필 이미지 업로드 중 오류가 발생했습니다.");
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

    // RestUserController에 추가할 메서드

    @PatchMapping("/user/{mbId}/update-with-image")
    public ResponseEntity<?> updateUserWithImage(@PathVariable("mbId") Long mbId,
                                                 @RequestPart("userUpdateDto") RequestUserUpdateDto updateDto,
                                                 @RequestPart(value = "file", required = false) MultipartFile file,
                                                 HttpServletRequest request) {
        try {
            System.out.println("[UNIFIED UPDATE DEBUG] =========================");
            System.out.println("[UNIFIED UPDATE DEBUG] 통합 업데이트 시작 - mbId: " + mbId);
            System.out.println("[UNIFIED UPDATE DEBUG] 받은 DTO: " + updateDto.toString());
            System.out.println("[UNIFIED UPDATE DEBUG] 파일 있음: " + (file != null && !file.isEmpty()));

            // 1. 권한 확인
            User authUser = access.getAuthenticatedUser(request);
            if (!authUser.getMbId().equals(mbId)) {
                log.warn("사용자 권한 없음 - 요청 mbId: {}, 로그인 사용자 ID: {}", mbId, authUser.getMbId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            // 2. 새 이미지가 있으면 업로드
            if (file != null && !file.isEmpty()) {
                System.out.println("[UNIFIED UPDATE DEBUG] 새 이미지 업로드 시작");
                String newProfileUrl = fileUpload.uploadProfileImage(file);
                updateDto.setMbProfileUrl(newProfileUrl);
                System.out.println("[UNIFIED UPDATE DEBUG] 새 이미지 URL: " + newProfileUrl);
            }

            // 3. 모든 사용자 정보 업데이트
            System.out.println("[UNIFIED UPDATE DEBUG] 프로필 업데이트 시작");
            userUpdateService.updateUserProfile(mbId, updateDto);
            System.out.println("[UNIFIED UPDATE DEBUG] 프로필 업데이트 완료");

            log.info("사용자 정보 통합 업데이트 성공 - 사용자 ID: {}", mbId);
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");

        } catch (IllegalArgumentException e) {
            System.out.println("[UNIFIED UPDATE ERROR] " + e.getMessage());
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            System.out.println("[UNIFIED UPDATE ERROR] " + e.getMessage());
            e.printStackTrace();
            log.error("서버 오류로 인해 사용자 정보 업데이트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보 업데이트 중 오류가 발생했습니다.");
        }
    }
}
