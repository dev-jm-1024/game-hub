package kr.plusb3b.games.gamehub.api.controller.login.rest;

import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.security.SecurityConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game0hub/api/v1/login")
public class RestLoginController {

    private final UserAuthRepository userRepo;
    private SecurityConfig securityConfig;

    public RestLoginController(UserAuthRepository userRepo, SecurityConfig securityConfig) {
        this.userRepo = userRepo;
        this.securityConfig = securityConfig;
    }

//    @PostMapping
//    public ResponseEntity checkLogin(@RequestBody UserAuth userAuth){
//        String id = userAuth.getAuth_user_id();
//        String rawPw = userAuth.getAuth_password();
//        String hashPw =
//    }
}
