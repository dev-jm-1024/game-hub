package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface UserUpdateService {

    void updateUserProfile(Long mbId, RequestUserUpdateDto userUpdateDto);
    void updateUserPassword(Long mbId, String currentPassword, String newPassword);

    // 새로 추가할 메서드
    String uploadAndUpdateProfileImage(Long mbId, MultipartFile file);

}