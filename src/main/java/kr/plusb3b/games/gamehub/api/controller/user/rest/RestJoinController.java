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
    private final SecurityConfig securityConfig;

    public RestJoinController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              UserAuthRepository userAuthRepository,
                              SecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthRepository = userAuthRepository;
        this.securityConfig = securityConfig;
    }


    @PostMapping("/join/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity registerUser(@RequestBody RequestUserJoinInfoDto requestUserJoinInfoDto) {

        String empty="";
        LocalDateTime noTime = null;
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        SnowflakeIdGenerator sfig = new SnowflakeIdGenerator(0,0);
        user.setMb_id(sfig.nextId());
        user.setMb_nickname(requestUserJoinInfoDto.getMb_nickname());
        user.setMb_profile_url(empty);
        user.setMb_status_message(empty);
        user.setMb_join_date(now);
        user.setMb_act(1);
        user.setMb_role(User.Role.USER);
        user.setMb_report_cnt(0);

        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setAuth_user_id(requestUserJoinInfoDto.getAuth_user_id());
        userAuth.setAuth_password(passwordEncoder.encode(
                requestUserJoinInfoDto.getAuth_password())
        );
        userAuth.setAuth_last_login(noTime);

        UserPrivate userPriv = new UserPrivate();
        SnowflakeIdGenerator sfigPri = new SnowflakeIdGenerator(1,0);
        userPriv.setPriMbId(sfigPri.nextId());
        userPriv.setUser(user);
        userPriv.setPri_email(requestUserJoinInfoDto.getPri_email());
        userPriv.setPri_birth(requestUserJoinInfoDto.getPri_birth());
        userPriv.setPri_gender(requestUserJoinInfoDto.getPri_gender());

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
