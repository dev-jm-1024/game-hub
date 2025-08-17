package kr.plusb3b.games.gamehub.domain.user.service;

import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import org.springframework.data.domain.Page;

public interface UserProvider {

    //User를 통해 UserAuth 존재여부 확인하기
    UserAuth hasUserAuth(User user);

    //User를 통해 UserPrivate 존재 확인하기
    UserPrivate hasUserPrivate(User user);

    //User를 통해 UserDetailsDto 반환 (단일 사용자)
    UserDetailsDto getUserDetails(User user);

    //User를 통해 권한 체크
    User.Role hasRole(User user);


    /************************************************ADMIN************************************************/


    // 페이징된 사용자 목록 조회
    Page<UserDetailsDto> getAllUsersDetails(int page, int size);

    // 기본 10개씩 조회
    Page<UserDetailsDto> getAllUsersDetails(int page);

    // 첫 번째 페이지 10개 조회
    Page<UserDetailsDto> getAllUsersDetails();

    // 상태별 사용자 조회
    Page<UserDetailsDto> getUsersByStatus(int status, int page, int size);

    // 역할별 사용자 조회
    Page<UserDetailsDto> getUsersByRole(User.Role role, int page, int size);

    // 닉네임으로 검색
    Page<UserDetailsDto> searchUsersByNickname(String nickname, int page, int size);
}