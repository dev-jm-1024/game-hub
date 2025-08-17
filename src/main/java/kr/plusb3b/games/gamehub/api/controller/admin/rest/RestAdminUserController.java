package kr.plusb3b.games.gamehub.api.controller.admin.rest;

import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1")
public class RestAdminUserController {

    private final AccessControlService access;
    private final UserProvider userProvider;
    private final UserRepository userRepo;

    public RestAdminUserController(AccessControlService access, UserProvider userProvider, UserRepository userRepo) {
        this.access = access;
        this.userProvider = userProvider;
        this.userRepo = userRepo;
    }

    //관리자 생성하기
    @PostMapping("/user/role/admin")
    public ResponseEntity<?> createAdmin(){

        return ResponseEntity.status(HttpStatus.OK).body("관리자 생성이 완료되었습니다");
    }

//    //회원 비활성화 시키기
//    @PostMapping("/user/{userId}/deactivate")
//    public ResponseEntity<?> deactivateUser(@PathVariable("userId") Long userId){
//
//
//
//
//    }


}
