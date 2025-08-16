package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import org.springframework.stereotype.Service;

@Service
public class UserProviderImpl implements UserProvider {

    @Override
    public UserAuth hasUserAuth(User user) {

        if(user == null) {return null;}

        return user.getUserAuth();
    }

    @Override
    public UserPrivate hasUserPrivate(User user) {
        if(user == null) {return null;}

        return user.getUserPrivate();
    }

    @Override
    public UserDetailsDto getUserDetails(User user) {
       if(user == null) {return null;}

       return new UserDetailsDto(
               user,
               hasUserAuth(user),
               hasUserPrivate(user)
       );
    }

    @Override
    public User.Role hasRole(User user) {
        return user.getMbRole();
    }
}
