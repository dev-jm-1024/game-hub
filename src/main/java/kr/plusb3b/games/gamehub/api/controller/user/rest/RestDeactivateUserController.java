package kr.plusb3b.games.gamehub.api.controller.user.rest;


import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
//@RequestMapping("/api/v1/game-hub")
//@RequestMapping("/game-hub/deactivate")
public class RestDeactivateUserController {

    private final UserAuthRepository userAuthRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;

    RestDeactivateUserController(UserAuthRepository userAuthRepo, PasswordEncoder passwordEncoder,
                                 UserRepository userRepo) {
        this.userAuthRepo = userAuthRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    // new API Path: /users/{userId}
    // @PathVariable("userId") Long userId, 나머지는 동일
    @PostMapping
    public ResponseEntity<?> deactivateUser(@RequestParam("authUserId")String authUserId,
                                            @RequestParam("authUserPassword") String authUserPassword) {


        boolean checkAuthUserId = authUserId == null || authUserId.equals("");
        boolean checkAuthUserPassword = authUserPassword == null || authUserPassword.equals("") ;


        //로그인한 아이디로 회원이 존재하는 지 확인
        Optional<UserAuth> userAuthOptional = userAuthRepo.findByAuthUserId(authUserId);

        //아이디 혹은 비밀번호가 공백일 경우, 올바르지 않다고 알림
        if(checkAuthUserId || checkAuthUserPassword)
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디 혹은 비밀번호가 공백입니다.");
        }

        if (userAuthOptional.isPresent())
        {
            UserAuth userAuth = userAuthOptional.get();
            if(passwordEncoder.matches(authUserPassword, userAuth.getAuthPassword())){
                try{
                    int isDeactivaeMember = userRepo.updateMbActByAuthUserId(authUserId);
                    if(isDeactivaeMember > 0){
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원 탈퇴에 성공했습니다! \n 30일간 정보가 보관되고, 사라집니다");
                    }else{
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("찾을 수 없음. 실패함");
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 탈퇴에 실패했습니다.");
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("탈퇴 처리 실패: 이미 탈퇴했거나 존재하지 않는 계정입니다.");

    }
}
