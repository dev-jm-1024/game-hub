package kr.plusb3b.games.gamehub.api.controller.user;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.user.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.dto.user.UserPrivate;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserPrivateRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/my-page")
public class UserController {

    private final AccessControlService access;
    private final UserRepository userRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final UserAuthRepository userAuthRepo;

    public UserController(AccessControlService access, UserRepository userRepo,
                          UserPrivateRepository userPrivateRepo, UserAuthRepository userAuthRepo) {
        this.access = access;
        this.userRepo = userRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.userAuthRepo = userAuthRepo;
    }


    //프로필 변경 페이지 요청 처리
    @GetMapping("/{mbId}/edit-profile")
    public String showEditProfilePage(@PathVariable("mbId") Long mbId, HttpServletRequest request, Model model) {

        User user = access.getAuthenticatedUser(request);
        Long memberId = user.getMbId();

        Optional<User> userOptional = userRepo.findById(memberId);
        Optional<UserAuth> userauthOptional = userAuthRepo.findAllByUserId(memberId);
        Optional<UserPrivate> userprivateOptional = userPrivateRepo.findUserPrivateByMbId(memberId);

        userOptional.ifPresent(value -> model.addAttribute("user", value));
        userauthOptional.ifPresent(value -> model.addAttribute("userAuth", value));
        userprivateOptional.ifPresent(value -> model.addAttribute("userPrivate", value));


        return "profile/profile-edit-form";
    }
}
