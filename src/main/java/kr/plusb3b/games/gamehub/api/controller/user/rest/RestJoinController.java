package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.api.dto.user.RequestUserJoinInfoDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.dto.user.UserPrivate;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserPrivateRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/user") //new API path
//@RequestMapping("/game-hub/api/v1")
public class RestJoinController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //추가
    private final UserAuthRepository userAuthRepository;
    private final UserPrivateRepository userPrivateRepository;


    public RestJoinController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              UserAuthRepository userAuthRepository,
                              UserPrivateRepository userPrivateRepository

                             ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthRepository = userAuthRepository;
        this.userPrivateRepository = userPrivateRepository;

    }

    //new API Path : /users
    //past API path: /join/new
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerUser(@RequestBody RequestUserJoinInfoDto requestUserJoinInfoDto) {

        String empty="";
        LocalDateTime noTime = null;
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        SnowflakeIdGenerator snowflake = new SnowflakeIdGenerator(0,0);
        user.setMbId(snowflake.nextId()); //mbId 생성
        user.setMbNickname(requestUserJoinInfoDto.getMbNickname()); //mbNickname 생성
        user.setMbProfileUrl(empty); //프로필 url 생성
        user.setMbStatusMessage(empty); //상태 메세지 생성
        user.setMbJoinDate(now); //가입날짜
        user.setMbAct(1); //mbAct 1로


        String prod = requestUserJoinInfoDto.getProd();
        if(prod.isEmpty()){
            user.setMbRole(User.Role.ROLE_USER);
        }
        else{
            user.setMbRole(User.Role.ROLE_PRODUCER);
        }

        user.setMbReportCnt(0);

        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setAuthUserId(requestUserJoinInfoDto.getAuthUserId());
        userAuth.setAuthPassword(passwordEncoder.encode(
                requestUserJoinInfoDto.getAuthPassword())
        );
        userAuth.setAuthLastLogin(noTime);

        UserPrivate userPrivate = new UserPrivate();
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1,0);
        userPrivate.setPriMbId(snowflakeIdGenerator.nextId());
        userPrivate.setUser(user);
        userPrivate.setPriEmail(requestUserJoinInfoDto.getPriEmail());
        userPrivate.setPriBirth(requestUserJoinInfoDto.getPriBirth());
        userPrivate.setPriGender(requestUserJoinInfoDto.getPriGender());

        try {
            userRepository.save(user);
            userAuthRepository.save(userAuth);
            userPrivateRepository.save(userPrivate);
            return ResponseEntity.ok("회원가입 성공");
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 실패: " + e.getMessage());
        }

    }
}
