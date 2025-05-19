package kr.plusb3b.games.gamehub.api.controller.login.rest;

import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> checkLogin(@ModelAttribute UserAuth userAuth) {
        Optional<UserAuth> userOptional = userAuthRepo.findByAuthUserId(userAuth.getAuthUserId());

        if (userOptional.isPresent()) {
            UserAuth found = userOptional.get();
            if (passwordEncoder.matches(userAuth.getAuthPassword(), found.getAuthPassword())) {
                String token = jwtProvider.createToken(found.getAuthUserId());

                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .body("로그인 성공: 토큰 발급 완료");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
    }

}
