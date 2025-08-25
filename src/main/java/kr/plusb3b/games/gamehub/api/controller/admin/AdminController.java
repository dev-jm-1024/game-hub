package kr.plusb3b.games.gamehub.api.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.io.IOException;


@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AccessControlService access;

    public AdminController(AccessControlService access) {
        this.access = access;
    }

    @GetMapping
    public String viewAdminPage(Model model, HttpServletRequest request){

        User user = access.getAuthenticatedUser(request);
        if(user == null) return "redirect: /game-hub/login";

        if(user.getMbRole() != User.Role.ROLE_ADMIN) {
            return "redirect:/game-hub";
        }

        model.addAttribute("admin", user);

        return "admin/index";
    }

    @GetMapping("/create")
    public String viewCreateAdminPage(Model model, HttpServletRequest request , HttpServletResponse response) throws IOException {

        access.validateAdminAccess(request, response);

        return "/admin/create-admin";
    }

}