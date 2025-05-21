package kr.plusb3b.games.gamehub.api.controller.login.rest;

import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/game-hub/api/v1/login")
public class RestLoginController {

    @Value("${app.api.version}")
    private String appApiVersion;

    private final UserAuthRepository userAuthRepo;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public RestLoginController(UserAuthRepository userAuthRepo,
                               PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userAuthRepo = userAuthRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    //로그인 체크
//    @PostMapping
//    public ResponseEntity checkLogin(@ModelAttribute  UserAuth userAuth){
//        String id = userAuth.getAuth_user_id();
//        String hashPw = passwordEncoder.encode(userAuth.getAuth_password());
//
//        Optional<UserAuth> userAuthOptional = userAuthRepo.findByUserAuth(id, hashPw);
//
//        try{
//            if(userAuthOptional.isPresent()){
//                return ResponseEntity.ok(userAuthOptional.get());
//            }else{
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    //로그인 체크
    @PostMapping
    public ResponseEntity<?> checkLogin(@RequestParam("authUserId") String authUserId,
                                        @RequestParam("authPassword") String authPassword,
                                        HttpServletResponse response) {

        //Optional<UserAuth> userOptional = userAuthRepo.findByAuthUserId(userAuth.getAuthUserId());

        boolean isAuthUserId = authUserId != null && !authUserId.isEmpty();
        boolean isAuthPassword = authPassword != null && !authPassword.isEmpty();

        if (isAuthUserId && isAuthPassword) {

            Optional<UserAuth> userAuthOptional = userAuthRepo.findByAuthUserId(authUserId);

            if(userAuthOptional.isPresent()) {
                UserAuth userAuth = userAuthOptional.get();

                if (passwordEncoder.matches(authPassword, userAuth.getAuthPassword())) {
                    String token = jwtProvider.createToken(userAuth.getAuthUserId());

                    // ✅ 쿠키로 JWT 내려주기
                    ResponseCookie cookie = ResponseCookie.from("jwt", token)
                            .httpOnly(true)
                            .secure(false) // ✅ 개발 시 false, 운영 시 true
                            .path("/")
                            .sameSite("Strict")
                            .maxAge(60 * 60) // 1시간
                            .build();

                    // ✅ 브라우저에 쿠키를 내려주는 핵심 한 줄!
                    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());



                    return ResponseEntity.ok()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .body("로그인 성공: 토큰 발급 완료");
                }
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디/비밀번호 누락");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
    }

}
