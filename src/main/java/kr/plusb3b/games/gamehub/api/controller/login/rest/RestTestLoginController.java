package kr.plusb3b.games.gamehub.api.controller.login.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.application.user.UserLoginServiceImpl;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserLoginInfo;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserLoginInfoRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import kr.plusb3b.games.gamehub.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
//@RequestMapping("/api/v1/auth")
//@RequestMapping("/game-hub/api/v1/login") : past API path
public class RestTestLoginController
{
//    @Value("${app.api.version}")
//    private String appApiVersion;
//
//    private final UserRepository userRepo;
//    private final UserAuthRepository userAuthRepo;
//    private final UserLoginInfoRepository userLoginInfoRepo;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtProvider jwtProvider;
//    private final UserLoginServiceImpl userLoginServiceImpl;
//
//    public RestTestLoginController(UserRepository userRepo, UserAuthRepository userAuthRepo, UserLoginInfoRepository userLoginInfoRepo, PasswordEncoder passwordEncoder,
//                               JwtProvider jwtProvider, UserLoginServiceImpl userLoginServiceImpl) {
//        this.userRepo = userRepo;
//        this.userAuthRepo = userAuthRepo;
//        this.userLoginInfoRepo = userLoginInfoRepo;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtProvider = jwtProvider;
//        this.userLoginServiceImpl = userLoginServiceImpl;
//    }
//
//    //로그인 체크
//    @PostMapping("/login")
//    public ResponseEntity<?> checkLogin(@RequestParam("authUserId") String authUserId,
//                                        @RequestParam("authPassword") String authPassword,
//                                        HttpServletRequest request,
//                                        HttpServletResponse response) {
//        // 유효성 검사: 빈 값
//        if (authUserId == null || authUserId.trim().isEmpty() ||
//                authPassword == null || authPassword.trim().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디 혹은 비밀번호가 누락되었습니다");
//        }
//
//        // 아이디 존재 여부 확인
//        Optional<UserAuth> userAuthOpt = userAuthRepo.findByAuthUserId(authUserId);
//        if (userAuthOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디입니다");
//        }
//
//        UserAuth userAuth = userAuthOpt.get();
//        User user = userAuth.getUser();
//
//        // 계정 활성화 여부 확인
//        if (!user.isActivateUser()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("탈퇴된 계정입니다");
//        }
//
//        // 비밀번호 일치 확인
//        if (!userLoginServiceImpl.isPasswordMatch(userAuth, authPassword)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다");
//        }
//
//        // JWT 토큰 생성 및 쿠키로 설정
//        userLoginServiceImpl.issueJwtCookie(response, userAuth.getAuthUserId());
//
//        // 로그인 기록 저장
//        userLoginServiceImpl.saveLoginHistory(user, request);
//
//        return ResponseEntity.ok("로그인 성공");
//    }
//
//

}
