package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.user.dto.FindUserDto;

import java.util.Optional;

public interface FindUserService {

    Optional<FindUserDto> findUserAuth(FindUserDto dto);

    Optional<String> getUserAuthId(FindUserDto dto);

}
