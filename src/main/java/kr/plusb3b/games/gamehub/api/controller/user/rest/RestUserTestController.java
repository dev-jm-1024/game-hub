package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.ChangePasswordDto;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/api/v1")
public class RestUserTestController {

    private AccessControlService access;
    private UserAuthRepository userAuthRepo;

    public RestUserTestController(AccessControlService access, UserAuthRepository userAuthRepo) {
        this.access = access;
        this.userAuthRepo = userAuthRepo;
    }


    @PostMapping("/user/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                            HttpServletRequest request) {




        return ResponseEntity.status(HttpStatus.OK).body("test");
    }


}
