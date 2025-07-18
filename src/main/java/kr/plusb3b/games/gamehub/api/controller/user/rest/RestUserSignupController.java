package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestUserSignupController {

    private final UserRepository userRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final UserJoinService userJoinService;

    public RestUserSignupController(UserRepository userRepo, UserAuthRepository userAuthRepo,
                                    UserPrivateRepository userPrivateRepo, UserJoinService userJoinService) {
        this.userRepo = userRepo;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.userJoinService = userJoinService;
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<?> registerUser (@RequestBody UserSignupDto userSignupDto){
//
//
//
//
//
//    }

}
