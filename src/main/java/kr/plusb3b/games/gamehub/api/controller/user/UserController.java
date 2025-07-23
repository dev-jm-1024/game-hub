package kr.plusb3b.games.gamehub.api.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
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
    private final UserProvider userProvider;

    public UserController(AccessControlService access, UserProvider userProvider) {
        this.access = access;
        this.userProvider = userProvider;
    }

    //프로필 변경 페이지 요청 처리
    @GetMapping("/edit-profile")
    public String showEditProfilePage(HttpServletRequest request, Model model) {

        User user = access.getAuthenticatedUser(request);
        UserDetailsDto userDetailsDto = userProvider.getUserDetails(user);
        model.addAttribute("userDetailsDto", userDetailsDto);

        return "profile/profile-edit-form";
    }
}
