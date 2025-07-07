package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserJoinServiceImpl implements UserJoinService {

    private final UserRepository userRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;

    public UserJoinServiceImpl(UserRepository userRepo, UserAuthRepository userAuthRepo, UserPrivateRepository userPrivateRepo) {
        this.userRepo = userRepo;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
    }

    @Override
    public void signupUser(UserSignupDto userSignupDto, UserSignupVO usv) {

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
}
