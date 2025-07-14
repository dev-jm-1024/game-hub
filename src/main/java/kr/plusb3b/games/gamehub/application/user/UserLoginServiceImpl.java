package kr.plusb3b.games.gamehub.application.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserLoginInfo;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserLoginInfoRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserLoginService;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import kr.plusb3b.games.gamehub.security.jwt.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    private final UserLoginInfoRepository userLoginInfoRepo;
    private final UserAuthRepository userAuthRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserLoginServiceImpl(UserLoginInfoRepository userLoginInfoRepo,
                                UserAuthRepository userAuthRepo,
                                PasswordEncoder passwordEncoder,
                                JwtProvider jwtProvider,
                                UserRepository userRepo) {
        this.userLoginInfoRepo = userLoginInfoRepo;
        this.userAuthRepo = userAuthRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    /** 로그인 ID로 UserAuth를 조회 */
    @Override
    public Optional<UserAuth> findUserAuthByLoginId(String loginId) {
        return userAuthRepo.findByAuthUserId(loginId);
    }

    /** UserAuth로부터 User 객체 반환 */
    @Override
    public Optional<User> getUserByAuth(UserAuth userAuth) {
        return Optional.ofNullable(userAuth.getUser());
    }

    @Override
    public boolean isPasswordMatch(UserAuth userAuth, String rawPassword) {
        if (userAuth == null || rawPassword == null) return false;
        return passwordEncoder.matches(rawPassword, userAuth.getAuthPassword());
    }


    /** JWT 토큰을 쿠키에 담아 응답에 추가 */
    @Override
    public void issueJwtCookie(HttpServletResponse response, String loginId) {
        String token = jwtProvider.createToken(loginId);
        ResponseCookie cookie = createJwtCookie(token);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** 로그인 기록 저장 */
    @Override
    public boolean saveLoginHistory(User user, HttpServletRequest request) {
        try {
            Long loginInfoId = new SnowflakeIdGenerator(0, 0).nextId();
            String clientIP = getClientIP(request);

            UserLoginInfo userLoginInfo = new UserLoginInfo(
                    user,
                    loginInfoId,
                    LocalDateTime.now(),
                    clientIP
            );

            userLoginInfoRepo.save(userLoginInfo);
            return true;
        } catch (Exception e) {
            logger.error("로그인 기록 저장 중 오류 발생", e);
            return false;
        }
    }

    /** 계정이 활성 상태인지 여부 확인 */
    @Override
    public boolean isUserActivated(User user) {
        return user != null && user.isActivateUser();
    }

    /** JWT 쿠키 생성 */
    private ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // 운영 환경에선 true
                .path("/")
                .sameSite("Strict")
                .maxAge(60 * 60) // 1시간
                .build();
    }

    /** 클라이언트 IP 추출 */
    private String getClientIP(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
