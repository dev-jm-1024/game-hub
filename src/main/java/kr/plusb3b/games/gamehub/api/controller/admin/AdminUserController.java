package kr.plusb3b.games.gamehub.api.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.user.dto.UserDetailsDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.service.UserProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/user-status")
public class AdminUserController {

    private final AccessControlService access;
    private final UserProvider userProvider;

    public AdminUserController(AccessControlService access, UserProvider userProvider) {
        this.access = access;
        this.userProvider = userProvider;
    }

    private final String REDIRECT_PATH = "redirect:/game-hub";

    @GetMapping
    public String viewUserStatusPage(Model model, HttpServletRequest request){

        User user = access.getAuthenticatedUser(request);
        if(user == null || user.getMbRole() != User.Role.ROLE_ADMIN) {
            return REDIRECT_PATH;
        }

        //User, UserPrivate, UserAuth 전부 담은 DTO
        UserDetailsDto userDetailsDto = userProvider.getUserDetails(user);
        model.addAttribute("userDetailsDto", userDetailsDto);

        return "admin/user-status/index";
    }
}
