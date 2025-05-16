package kr.plusb3b.games.gamehub.api.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game-hub")
public class LoginController {

    //로그인 페이지로 이동
    @GetMapping("/login")
    public String showLoginPage() {
        return "login-form";
    }

}
