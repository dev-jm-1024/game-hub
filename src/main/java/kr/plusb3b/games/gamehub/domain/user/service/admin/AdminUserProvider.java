package kr.plusb3b.games.gamehub.domain.user.service.admin;

import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserLoginInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminUserProvider {


//    // 페이징된 사용자 목록 조회
//    Page<UserDetailsDto> getAllUsersDetails(int page, int size);
//
//    // 기본 10개씩 조회
//    Page<UserDetailsDto> getAllUsersDetails(int page);
//
//    // 첫 번째 페이지 10개 조회
//    Page<UserDetailsDto> getAllUsersDetails();
//
//    // 상태별 사용자 조회
//    Page<UserDetailsDto> getUsersByStatus(int status, int page, int size);
//
//    // 역할별 사용자 조회
//    Page<UserDetailsDto> getUsersByRole(User.Role role, int page, int size);
//
//    // 닉네임으로 검색
//    Page<UserDetailsDto> searchUsersByNickname(String nickname, int page, int size);

    List<UserLoginInfo> getUserLoginInfo(Long mbId);
}
