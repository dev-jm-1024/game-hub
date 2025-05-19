package kr.plusb3b.games.gamehub.api.controller.user.rest;
import kr.plusb3b.games.gamehub.api.dto.user.RequestUserJoinInfoDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.dto.user.UserPrivate;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.SecurityConfig;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/game-hub/api/v1")
public class RestJoinController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 🔐 추가
    private final UserAuthRepository userAuthRepository;

    public RestJoinController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              UserAuthRepository userAuthRepository
                             ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthRepository = userAuthRepository;
    }


    @PostMapping("/join/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity registerUser(@RequestBody RequestUserJoinInfoDto requestUserJoinInfoDto) {

        String empty="";
        LocalDateTime noTime = null;
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        SnowflakeIdGenerator sfig = new SnowflakeIdGenerator(0,0);
        user.setMbId(sfig.nextId());
        user.setMbNickname(requestUserJoinInfoDto.getMb_nickname());
        user.setMbProfileUrl(empty);
        user.setMbStatusMessage(empty);
        user.setMbJoinDate(now);
        user.setMbAct(1);
        user.setMbRole(User.Role.ROLE_USER);
        user.setMbReportCnt(0);

        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setAuthUserId(requestUserJoinInfoDto.getAuth_user_id());
        userAuth.setAuthPassword(passwordEncoder.encode(
                requestUserJoinInfoDto.getAuth_password())
        );
        userAuth.setAuthLastLogin(noTime);

        UserPrivate userPriv = new UserPrivate();
        SnowflakeIdGenerator sfigPri = new SnowflakeIdGenerator(1,0);
        userPriv.setPriMbId(sfigPri.nextId());
        userPriv.setUser(user);
        userPriv.setPriEmail(requestUserJoinInfoDto.getPri_email());
        userPriv.setPriBirth(requestUserJoinInfoDto.getPri_birth());
        userPriv.setPriGender(requestUserJoinInfoDto.getPri_gender());

        try {
            userRepository.save(user);
            userAuthRepository.save(userAuth);
            return ResponseEntity.ok("회원가입 성공");
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 실패: " + e.getMessage());
        }

    }
}
