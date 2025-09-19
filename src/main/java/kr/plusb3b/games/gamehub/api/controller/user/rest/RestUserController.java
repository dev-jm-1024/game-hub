package kr.plusb3b.games.gamehub.api.controller.user.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.plusb3b.games.gamehub.common.util.FileValidator;
import kr.plusb3b.games.gamehub.domain.user.dto.ChangePasswordDto;
import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.service.UserDeleteService;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import kr.plusb3b.games.gamehub.domain.user.service.UserUpdateService;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthPassword;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthUserId;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    //WebGL 에서 사용자 정보를 요청할 때 응답하는  : /user

    //사용자의 쿠키에서 mbId 추출
    //직렬화 통해서 JSON 으로 반환


    @PostMapping("/user/{mbId}/profile-image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable("mbId") Long mbId,
                                                @RequestPart("file") MultipartFile file,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {

        // 1. 인증된 사용자 확인
        access.validateUserAccess(request, response);

        //2. 파일 유효성 검사
        FileValidator.validateImage(file);

        // 3. 프로필 이미지 업로드 처리
        String profileImageUrl = userUpdateService.uploadAndUpdateProfileImage(mbId, file);

        // 4. 성공 응답
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("profileUrl", profileImageUrl);
        successResponse.put("message", "프로필 이미지가 성공적으로 업데이트되었습니다.");

        log.info("프로필 이미지 업로드 성공 - 사용자 ID: {}, URL: {}", mbId, profileImageUrl);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        access.validateUserAccess(request, response);
        User user = access.getAuthenticatedUser(request);

        log.info("비밀번호 변경 요청 - 사용자 ID: {}", user.getMbId());

        userUpdateService.updateUserPassword(
                user.getMbId(),
                changePasswordDto
        );

        log.info("비밀번호 변경 성공 - 사용자 ID: {}", user.getMbId());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");

    }


    @PatchMapping("/user/{mbId}/deactivate")
    public ResponseEntity<?> deactivateUserStatus(@RequestParam("authUserId")String authUserId,
                                            @RequestParam("authUserPassword") String authUserPassword,
                                            HttpServletRequest request,
                                                  HttpServletResponse response) throws IOException {

        // 1. 필수 입력값 확인
        AuthUserId.of(authUserId);
        AuthPassword.of(authUserPassword);


        // 3. 로그인된 사용자 확인
        access.validateUserAccess(request, response);
        User currentUser = access.getAuthenticatedUser(request);

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
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        try {
            // 1. 권한 확인
            access.validateUserAccess(request, response);

            // 2. 새 이미지가 있으면 업로드
            if (file != null && !file.isEmpty()) {
                String newProfileUrl = fileUpload.uploadProfileImage(file);
                updateDto.setMbProfileUrl(newProfileUrl);
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
