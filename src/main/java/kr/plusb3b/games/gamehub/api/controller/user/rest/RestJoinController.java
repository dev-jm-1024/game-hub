package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserJoinInfoDto;
import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/user") //new API path
//@RequestMapping("/game-hub/api/v1")
public class RestJoinController {

    private final UserJoinService userJoinService;

    public RestJoinController(UserJoinService userJoinService) {
        this.userJoinService = userJoinService;
    }

    //new API Path : /users
    //past API path: /join/new
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerUser(@RequestBody UserSignupDto userSignupDto) {
        try {
            userJoinService.signupUser(userSignupDto, new UserSignupVO()); // VO는 내부에서 채워질 수 있도록 빈 객체 전달
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 사용자입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 실패: " + e.getMessage());
        }
    }
}
