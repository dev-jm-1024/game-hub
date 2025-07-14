package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserUpdateService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final PasswordEncoder passwordEncoder;

    public UserUpdateServiceImpl(UserRepository userRepository, UserAuthRepository userAuthRepo,
                                 UserPrivateRepository userPrivateRepo, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void updateUserProfile(Long mbId, RequestUserUpdateDto userUpdateDto) {

        User user = userRepository.findById(mbId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserPrivate userPrivate = userPrivateRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("UserPrivate not found"));

//        UserAuth userAuth = userAuthRepo.findByUser(user)
//                .orElseThrow(() -> new IllegalArgumentException("UserAuth not found"));

        user.changeMbNickName(userUpdateDto.getMbNickName());
        user.changeMbProfileUrl(userUpdateDto.getMbProfileUrl());
        user.changeMbStatusMessage(userUpdateDto.getMbStatusMessage());

        userPrivate.changeBirth(userUpdateDto.getPriBirth());
        userPrivate.changeEmail(userUpdateDto.getPriEmail());
        userPrivate.changeGender(userUpdateDto.getPriGender());
    }

    @Override
    @Transactional
    public void updateUserPassword(Long mbId, String currentPassword, String newPassword) {
        User user = userRepository.findById(mbId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserAuth userAuth = userAuthRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("UserAuth not found"));
        if(!passwordEncoder.matches(currentPassword, newPassword)){
            throw new IllegalArgumentException("Passwords do not match");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        userAuth.changePassword(encodedPassword);

    }
}
