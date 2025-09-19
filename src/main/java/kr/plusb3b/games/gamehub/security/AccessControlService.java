package kr.plusb3b.games.gamehub.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.*;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthUserId;
import kr.plusb3b.games.gamehub.security.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class AccessControlService {

    private final JwtProvider jwtProvider;
    private final UserAuthRepository userAuthRepo;

    public AccessControlService(JwtProvider jwtProvider, UserAuthRepository userAuthRepo) {
        this.jwtProvider = jwtProvider;
        this.userAuthRepo = userAuthRepo;
    }

    public User getAuthenticatedUser(HttpServletRequest request) {

        // 1. JWT 쿠키 추출
        Cookie[] cookies = request.getCookies();
        String jwt = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // 2. JWT 존재 여부 확인
        if (jwt == null || !jwtProvider.validateToken(jwt)) {
            return null;
        }

        // 3. JWT에서 사용자 ID 추출
        String authUserId = jwtProvider.getUserId(jwt);

        // 4. 사용자 조회 --- 여기서 오류남
//        User user1 = userRepo.findByUserAuth_AuthUserId(authUserId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional<UserAuth> userAuthOpt = userAuthRepo.findByAuthUserId(AuthUserId.of(authUserId));
        if(!(userAuthOpt.isPresent())) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
        }

        User user = userAuthOpt.get().getUser();
        // 5. 탈퇴 여부 확인
        if (user.getMbAct() == 0) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        return user;
    }

    // AccessControlService.java
    public void validateAdminAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = getAuthenticatedUser(request);

        if(user == null) {
            response.sendRedirect("/login");
            return;
        }

        if(user.getMbRole() != User.Role.ROLE_ADMIN) {
            response.sendRedirect("/access-denied");
            return;
        }
    }

    public void validateUserAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = getAuthenticatedUser(request);

        if(user == null) {
            response.sendRedirect("/login");
            return;
        }

        if(user.getMbRole() != User.Role.ROLE_USER) {
            response.sendRedirect("/");
            return;
        }
    }


}
