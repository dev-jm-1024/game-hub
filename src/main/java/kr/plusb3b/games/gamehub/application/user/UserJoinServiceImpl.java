package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserJoinServiceImpl implements UserJoinService {

    private final UserRepository userRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final PasswordEncoder passwordEncoder;

    public UserJoinServiceImpl(UserRepository userRepo, UserAuthRepository userAuthRepo, UserPrivateRepository userPrivateRepo,
                               PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signupUser(UserSignupDto usd, UserSignupVO usv) {

        String prodDifferentiate = usd.getProd();
        User.Role role;

        if(prodDifferentiate.equals("generalUser")){
            role = (User.Role.ROLE_USER);
        }else{
            role = (User.Role.ROLE_PRODUCER);
        }

        User user = buildUser(usd, usv, role);
        UserAuth userAuth = buildUserAuth(usd, user);
        UserPrivate userPrivate = buildUserPrivate(usd, user);

        userRepo.save(user);
        userAuthRepo.save(userAuth);
        userPrivateRepo.save(userPrivate);
    }

    @Override
    public boolean isLogin(User user) {
        return false;
    }

    @Override
    public boolean checkDistinctLoginId(String loginId) {
        return false;
    }

    @Override
    public boolean checkDistinctEmail(String email) {
        return false;
    }


    private UserAuth buildUserAuth(UserSignupDto userSignupDto, User user) {

        return new UserAuth(
                userSignupDto.getAuthUserId(),
                user,
                passwordEncoder.encode(userSignupDto.getAuthPassword()),
                null
        );

    }

    private User buildUser(UserSignupDto userSignupDto, UserSignupVO userSignupVO, User.Role role) {

        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(0,0);
        Long mbId = snowflakeIdGenerator.nextId();

        return new User(
                userSignupVO.getMbReportCnt(),
                mbId,
                userSignupDto.getMbNickname(),
                "", //프로필 url 들어갈 예정
                "", //상태 메세지 들어갈 예정
                LocalDateTime.now(),
                userSignupVO.getMbAct(),
                role
        );

    }

    private UserPrivate buildUserPrivate(UserSignupDto userSignupDto, User user) {

        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1,0);
        Long priMbId = snowflakeIdGenerator.nextId();

        return new UserPrivate(
                priMbId,
                user,
                userSignupDto.getPriEmail(),
                userSignupDto.getPriBirth(),
                userSignupDto.getPriGender()
        );
    }

}
