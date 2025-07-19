package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;

public interface UserProvider {

    //User를 통해 UserAuth 존재여부 확인하기
    UserAuth hasUserAuth(User user);

    //User를 통해 UserPrivate 존재 확인하기
    UserPrivate hasUserPrivate(User user);

    //User를 통해 UserDetailsDto 반환
    UserDetailsDto getUserDetails(User user);
}
