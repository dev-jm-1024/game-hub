package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.dto.FindUserDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.FindUserService;
import kr.plusb3b.games.gamehub.domain.user.vo.business.MbNickName;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriEmail;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserServiceImpl implements FindUserService {

    private final UserRepository userRepo;
    private final UserPrivateRepository userPrivateRepo;


    public FindUserServiceImpl(UserRepository userRepo, UserPrivateRepository userPrivateRepo) {
        this.userRepo = userRepo;
        this.userPrivateRepo = userPrivateRepo;
    }

    @Override
    public Optional<FindUserDto> findUserAuth(FindUserDto dto) {
        boolean userExists = userPrivateRepo.findUserPrivateByPriEmail(PriEmail.of(dto.getEmail())).isPresent()
                && userRepo.findUsersByMbNickName(MbNickName.of(dto.getName())).isPresent();

        return userExists ? Optional.of(dto) : Optional.empty();
    }

}
