package kr.plusb3b.games.gamehub.api.controller.login.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.dto.user.UserLoginInfo;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserLoginInfoRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.SecurityConfig;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
//@RequestMapping("/game-hub/api/v1/login") : past API path
public class RestLoginController
{

    private final UserRepository userRepository;
    @Value("${app.api.version}")
    private String appApiVersion;

    private final UserAuthRepository userAuthRepo;
    private final UserLoginInfoRepository loginInfoRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;



    public RestLoginController(UserAuthRepository userAuthRepo,
                               PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
                               UserLoginInfoRepository loginInfoRepo, UserRepository userRepository) {
        this.userAuthRepo = userAuthRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.loginInfoRepo = loginInfoRepo;
        this.userRepository = userRepository;
    }

    //로그인 체크
    @PostMapping("/login")
    //new API Path : /login
    public ResponseEntity<?> checkLogin(@RequestParam("authUserId") String authUserId,
                                        @RequestParam("authPassword") String authPassword,
                                        HttpServletResponse response, HttpServletRequest request) {

        //Optional<UserAuth> userOptional = userAuthRepo.findByAuthUserId(userAuth.getAuthUserId());

        boolean isAuthUserId = authUserId != null && !authUserId.isEmpty();
        boolean isAuthPassword = authPassword != null && !authPassword.isEmpty();


        //이때 로그인 시도하려는 아이디의 값이 mbAct=0 이면 회원 탈퇴로 판단
        Optional<Integer> isMemberBreak = userRepository.findMbActByAuthUserId(authUserId);

        if(isMemberBreak == null){return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원조회가 안되어짐");}
        if(isMemberBreak.isPresent()){
            Integer result = isMemberBreak.get();

            if(result == 0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("탈퇴함");
            }
        }



        if (isAuthUserId && isAuthPassword)
        {

            //로그인한 아이디로 회원이 존재하는 지 확인
            Optional<UserAuth> userAuthOptional = userAuthRepo.findByAuthUserId(authUserId);

            if(userAuthOptional.isPresent())
            {
                System.out.println("UserAuth 존재함?" + userAuthOptional.isPresent());  //true 나옴
                UserAuth userAuth = userAuthOptional.get();

                if (passwordEncoder.matches(authPassword, userAuth.getAuthPassword()))
                {
                    System.out.println("비밀번호 matches결과: "+passwordEncoder.matches(authPassword, userAuth.getAuthPassword()));
                    String token = jwtProvider.createToken(userAuth.getAuthUserId());

                    //쿠키로 JWT 내려주기
                    ResponseCookie cookie = ResponseCookie.from("jwt", token)
                            .httpOnly(true)
                            .secure(false) // ✅ 개발 시 false, 운영 시 true
                            .path("/")
                            .sameSite("Strict")
                            .maxAge(60 * 60) // 1시간
                            .build();

                    //브라우저에 쿠키를 내려줌
                    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                    //로그인 기록 고유 아이디 생성
                    SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(0,0);
                    Long loginInfoId = snowflakeIdGenerator.nextId();

                    //로그인 시각 생성
                    LocalDateTime loginTime = LocalDateTime.now();
                    System.out.println("로그인 시각" + loginTime);

                    //접속 IP주소 획득
                    String ipAddress = getClientIP(request);

                    //로그인 한 아이디로 사용자 고유 아이디 찾기
                    UserLoginInfo userLoginInfo = new UserLoginInfo();
                    Optional<User> userOptional = userRepository.findUserByUserAuth_authUserId(authUserId);

                    if(userOptional.isPresent()){
                        User user = userOptional.get();

                        //UserLoginInfo 조립 시작
                        userLoginInfo.setLoginInfoId(loginInfoId);
                        userLoginInfo.setLoginTime(loginTime);
                        userLoginInfo.setUser(user);
                        userLoginInfo.setIpAddress(ipAddress);
                    }else{
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User 객체 비어있음");
                    }

                    try{
                        //로그인 기록 저장
                        loginInfoRepo.save(userLoginInfo);

                        //UserAuth의 authLastLogin 기록 업데이트
                        userAuthRepo.updateLastLoginByUserId(loginTime, authUserId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    return ResponseEntity.ok()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .body("로그인 성공: 토큰 발급 완료");
                }
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디/비밀번호 누락");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
    }

    //클라이언트의 로그인한 IP얻는 메소드
    public String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }



}
