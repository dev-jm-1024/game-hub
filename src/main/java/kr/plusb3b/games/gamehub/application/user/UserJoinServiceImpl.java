package kr.plusb3b.games.gamehub.application.user;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserJoinServiceImpl implements UserJoinService {

    private final UserRepository userRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccessControlService access;

    public UserJoinServiceImpl(UserRepository userRepo, UserAuthRepository userAuthRepo, UserPrivateRepository userPrivateRepo,
                               PasswordEncoder passwordEncoder, AccessControlService access) {
        this.userRepo = userRepo;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.passwordEncoder = passwordEncoder;
        this.access = access;
    }

    @Override
    public void signupUser(UserSignupDto usd, UserSignupVO usv) {

        User ddd = new User();
        User.Role role = ddd.prodDifferentiate(usd.getProd());

        User user = buildUser(usd, usv, role);
        UserAuth userAuth = buildUserAuth(usd, user);
        UserPrivate userPrivate = buildUserPrivate(usd, user);

        userRepo.save(user);
        userAuthRepo.save(userAuth);
        userPrivateRepo.save(userPrivate);

    }

    @Override
    public boolean isLogin(HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);
        return user != null;
    }

    @Override
    public boolean isDuplicatedLoginId(String loginId) {
        Optional<UserAuth> userAuthOpt = userAuthRepo.findById(loginId);
        return userAuthOpt.isPresent();

    }

    @Override
    public boolean checkDistinctEmail(String email) {
        return userPrivateRepo.findAll().stream()
                .map(UserPrivate::getPriEmail)
                .anyMatch(e -> e.equals(email));  // 이메일이 하나도 없으면 true (중복 X)
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
