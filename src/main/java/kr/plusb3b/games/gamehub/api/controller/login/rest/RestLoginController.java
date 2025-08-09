package kr.plusb3b.games.gamehub.api.controller.login.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.application.user.UserLoginServiceImpl;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class RestLoginController {

    @Value("${app.api.version}")
    private String appApiVersion;

    private final UserAuthRepository userAuthRepo;
    private final UserLoginServiceImpl userLoginService;

    public RestLoginController(UserAuthRepository userAuthRepo, UserLoginServiceImpl userLoginService) {
        this.userAuthRepo = userAuthRepo;
        this.userLoginService = userLoginService;
    }

    // 로그인 체크
    @PostMapping("/login")
    public ResponseEntity<?> checkLogin(@RequestParam("authUserId") String authUserId,
                                        @RequestParam("authPassword") String authPassword,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        // 1. 유효성 검사: 빈 값 체크
        if (authUserId == null || authUserId.trim().isEmpty() ||
                authPassword == null || authPassword.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("아이디 혹은 비밀번호가 누락되었습니다");
        }

        // 2. 아이디를 통해 DB에 존재하는지 확인
        Optional<UserAuth> userAuthOpt = userLoginService.findUserAuthByLoginId(authUserId);
        if (userAuthOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("아이디가 존재하지 않습니다.");
        }

        UserAuth userAuth = userAuthOpt.get();

        // 3. 사용자 정보 가져오기
        Optional<User> userOpt = userLoginService.getUserByAuth(userAuth);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOpt.get();

        // 4. 계정의 활성화 상태 확인
        if (!userLoginService.isUserActivated(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("현재 탈퇴된 상태입니다. 관리자에게 문의해주세요.");
        }

        // 5. 비밀번호 검증
        if (!userLoginService.isPasswordMatch(userAuth, authPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("비밀번호가 일치하지 않습니다.");
        }

        // 6. 로그인 성공 처리
        try {
            // JWT 토큰 발급 및 쿠키 설정
            userLoginService.issueJwtCookie(response, authUserId);

            // 로그인 기록 저장
            boolean isLoginHistorySaved = userLoginService.saveLoginHistory(user, request);
            if (!isLoginHistorySaved) {
                // 로그인 기록 저장 실패는 로그만 남기고 로그인은 성공으로 처리
                System.err.println("로그인 기록 저장에 실패했습니다. 사용자 ID: " + authUserId);
            }

            // 성공 응답에 사용자 역할 정보 포함
            Map<String, Object> loginResponse = new HashMap<>();
            loginResponse.put("message", "로그인 성공");
            loginResponse.put("role", user.getMbRole().toString());
            loginResponse.put("nickname", user.getMbNickname());

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            System.err.println("로그인 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다.");
        }
    }
}