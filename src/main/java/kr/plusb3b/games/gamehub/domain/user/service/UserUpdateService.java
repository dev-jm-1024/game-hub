package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;

public interface UserUpdateService {

    void updateUserProfile(Long mbId, RequestUserUpdateDto userUpdateDto);

    void updateUserPassword(Long mbId, String currentPassword, String newPassword);

}
