package kr.plusb3b.games.gamehub.api.controller.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.security.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/my-page")
public class UserController {

    private final AccessControlService access;
    private final UserRepository userRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final UserAuthRepository userAuthRepo;
    private final JwtProvider jwtProvider;

    public UserController(AccessControlService access, UserRepository userRepo,
                          UserPrivateRepository userPrivateRepo, UserAuthRepository userAuthRepo,
                          JwtProvider jwtProvider) {
        this.access = access;
        this.userRepo = userRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.userAuthRepo = userAuthRepo;
        this.jwtProvider = jwtProvider;
    }


    //프로필 변경 페이지 요청 처리
    @GetMapping("/edit-profile")
    public String showEditProfilePage(HttpServletRequest request, Model model) {

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
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 3. JWT에서 사용자 ID 추출
        String authUserId = jwtProvider.getUserId(jwt);
        System.out.println("Debugging Check AuthUserId: " + authUserId);

        // 4. 사용자 조회 --- 여기서 오류남
        UserAuth userAuth = userAuthRepo.findById(authUserId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        User user = userAuth.getUser();

        // 5. 탈퇴 여부 확인
        if (user.getMbAct() == 0) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        Long memberId = user.getMbId();


        Optional<User> userOptional = userRepo.findById(memberId);
        Optional<UserAuth> userauthOptional = userAuthRepo.findByUser_MbId(memberId);
        Optional<UserPrivate> userprivateOptional = userPrivateRepo.findUserPrivateByMbId(memberId);

        userOptional.ifPresent(value -> model.addAttribute("user", value));
        userauthOptional.ifPresent(value -> model.addAttribute("userAuth", value));
        userprivateOptional.ifPresent(value -> model.addAttribute("userPrivate", value));


        return "profile/profile-edit-form";
    }
}
