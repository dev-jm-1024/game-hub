package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserProviderImpl implements UserProvider {

    private final UserRepository userRepository;

    public UserProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserAuth hasUserAuth(User user) {
        if(user == null) {
            return null;
        }
        return user.getUserAuth();
    }

    @Override
    public UserPrivate hasUserPrivate(User user) {
        if(user == null) {
            return null;
        }
        return user.getUserPrivate();
    }

    @Override
    public UserDetailsDto getUserDetails(User user) {
        if(user == null) {
            return null;
        }

        return new UserDetailsDto(
                user,
                hasUserAuth(user),
                hasUserPrivate(user)
        );
    }

    @Override
    public User.Role hasRole(User user) {
        if(user == null) {
            return null;
        }
        return user.getMbRole();
    }

    @Override
    public Page<UserDetailsDto> getAllUsersDetails(int page, int size) {
        // 페이지는 0부터 시작, 최신 가입자부터 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by("mbJoinDate").descending());

        Page<User> usersPage = userRepository.findAll(pageable);

        // User -> UserDetailsDto 변환
        return usersPage.map(user -> new UserDetailsDto(
                user,
                user.getUserAuth(),
                user.getUserPrivate()
        ));
    }

    @Override
    public Page<UserDetailsDto> getAllUsersDetails(int page) {
        return getAllUsersDetails(page, 10);
    }

    @Override
    public Page<UserDetailsDto> getAllUsersDetails() {
        return getAllUsersDetails(0, 10);
    }

    @Override
    public Page<UserDetailsDto> getUsersByStatus(int status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("mbJoinDate").descending());

        Page<User> usersPage = userRepository.findByMbAct(status, pageable);

        return usersPage.map(user -> new UserDetailsDto(
                user,
                user.getUserAuth(),
                user.getUserPrivate()
        ));
    }

    @Override
    public Page<UserDetailsDto> getUsersByRole(User.Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("mbJoinDate").descending());

        Page<User> usersPage = userRepository.findByMbRole(role, pageable);

        return usersPage.map(user -> new UserDetailsDto(
                user,
                user.getUserAuth(),
                user.getUserPrivate()
        ));
    }

    @Override
    public Page<UserDetailsDto> searchUsersByNickname(String nickname, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("mbJoinDate").descending());

        Page<User> usersPage = userRepository.findByMbNickName_MbNickNameContaining(nickname, pageable);

        return usersPage.map(user -> new UserDetailsDto(
                user,
                user.getUserAuth(),
                user.getUserPrivate()
        ));
    }
}