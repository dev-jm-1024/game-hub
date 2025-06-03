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
    @Value("${app.api.version}")
    private String appApiVersion;

    private final UserRepository userRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserLoginInfoRepository userLoginInfoRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public RestLoginController(UserRepository userRepo, UserAuthRepository userAuthRepo, UserLoginInfoRepository userLoginInfoRepo, PasswordEncoder passwordEncoder,
                               JwtProvider jwtProvider) {
        this.userRepo = userRepo;
        this.userAuthRepo = userAuthRepo;
        this.userLoginInfoRepo = userLoginInfoRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    //로그인 체크
    @PostMapping("/login")
    //new API Path : /login
    public ResponseEntity<?> checkLogin(@RequestParam("authUserId") String authUserId,
                                        @RequestParam("authPassword") String authPassword,HttpServletRequest request,
                                        HttpServletResponse response) {

        boolean checkId = authUserId == null || authUserId.length() == 0;
        boolean checkPassword = authPassword == null || authPassword.length() == 0;

        //데이터 누락 체크
        if(checkId && checkPassword) {return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("아이디 혹은 비밀번호가 누락되었습니다");}


        //Optional<User> userOpt = userRepo.findByAuthUserId(authUserId); //이 코드가 문제
        Optional<UserAuth> userAuthOpt = userAuthRepo.findById(authUserId);

        //디버기 용도
        //boolean checkUserOpt = userOpt.isPresent(); //이 친구가 조회가 안되어짐
        boolean checkUserAuthOpt = userAuthOpt.isPresent();

        //System.out.println("user 조회여부: " + checkUserOpt);
        System.out.println("userAuth 조회여부: " + checkUserAuthOpt);

        //로그인한 아이디가 DB에 존재하는 지 확인
        if(checkUserAuthOpt)
        {
            UserAuth userAuth = userAuthOpt.get();
            User user = userAuth.getUser();
            int mbAct = user.getMbAct();

            //회원의 계정상태 검사
            if(mbAct == 0) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("탈퇴된 계정입니다.");

            //해시 값 일치하는 지 확인
            if(passwordEncoder.matches(authPassword, userAuth.getAuthPassword())){

                //일치할 경우 토큰 발급
                /***************************************************************************************/
                String token = jwtProvider.createToken(userAuth.getAuthUserId());

                //쿠키로 JWT 내려주기
                ResponseCookie cookie = ResponseCookie.from("jwt", token)
                        .httpOnly(true)
                        .secure(false) // 개발 시 false, 운영 시 true
                        .path("/")
                        .sameSite("Strict")
                        .maxAge(60 * 60) // 1시간
                        .build();

                //브라우저에 쿠키를 내려줌
                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                /***************************************************************************************/

                /***************************************************************************************/
                //로그인 기록 남기기

                //UserLoginInfo 조립 시작

                //로그인 기록 고유 아이디 생성
                SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(0,0);
                Long loginInfoId = snowflakeIdGenerator.nextId();

                //현재시간 생성
                LocalDateTime loginTime = LocalDateTime.now();

                //접속 IP 획득
                String ipAddress = getClientIP(request);

                UserLoginInfo userLoginInfo = new UserLoginInfo();
                userLoginInfo.setLoginInfoId(loginInfoId);
                userLoginInfo.setLoginTime(loginTime);
                userLoginInfo.setUser(user);
                userLoginInfo.setIpAddress(ipAddress);
                /***************************************************************************************/

                try{
                    userLoginInfoRepo.save(userLoginInfo);
                    return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                }

            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다");

            }

        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("알 수 없는 이유로 실패하였습니다");
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
