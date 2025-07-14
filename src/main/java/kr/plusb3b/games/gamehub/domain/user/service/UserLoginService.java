package kr.plusb3b.games.gamehub.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

public interface UserLoginService {

    // 로그인 아이디로 사용자 인증 정보 조회
    Optional<UserAuth> findUserAuthByLoginId(String loginId);

    // UserAuth로부터 User 반환
    Optional<User> getUserByAuth(UserAuth userAuth);

    // 비밀번호 해시 일치 확인 (리팩토링 후 새 메소드)
    boolean isPasswordMatch(UserAuth userAuth, String rawPassword);


    // JWT 토큰을 쿠키에 담아 응답에 세팅
    void issueJwtCookie(HttpServletResponse response, String loginId);

    // 로그인 기록 저장
    boolean saveLoginHistory(User user, HttpServletRequest request);

    // 계정 활성 여부 확인
    boolean isUserActivated(User user);
}
