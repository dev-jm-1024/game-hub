package kr.plusb3b.games.gamehub.api.service.User;

import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserSignupDto;
import kr.plusb3b.games.gamehub.api.dto.user.UserSignupVO;
import org.springframework.stereotype.Service;

@Service
public class UserJoinServiceImpl implements UserJoinService {

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
