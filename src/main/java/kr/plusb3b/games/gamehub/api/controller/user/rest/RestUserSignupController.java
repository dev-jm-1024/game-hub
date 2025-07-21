package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class RestUserSignupController {

//    private final UserJoinService userJoinService;
//
//    public RestUserSignupController(UserJoinService userJoinService) {
//        this.userJoinService = userJoinService;
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<?> registerUser(@RequestBody UserSignupDto userSignupDto) {
//        try {
//            userJoinService.signupUser(userSignupDto, new UserSignupVO()); // VO는 내부에서 채워질 수 있도록 빈 객체 전달
//            return ResponseEntity.ok("회원가입 성공");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청: " + e.getMessage());
//        } catch (DataIntegrityViolationException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 사용자입니다.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("회원가입 실패: " + e.getMessage());
//        }
//    }
//

}
