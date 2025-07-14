package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserDeleteService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDeleteServiceImpl implements UserDeleteService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthRepository userAuthRepo;

    public UserDeleteServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder,
                                 UserAuthRepository userAuthRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.userAuthRepo = userAuthRepo;
    }

    @Override
    @Transactional
    public boolean deactivateUser(long mbId, String password) {

        User user = userRepo.findById(mbId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        UserAuth userAuth = userAuthRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("인증 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, userAuth.getAuthPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.deactivateUser();
        return true;
    }
}
