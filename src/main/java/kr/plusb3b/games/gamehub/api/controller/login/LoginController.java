package kr.plusb3b.games.gamehub.api.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game-hub/login")
public class LoginController {


    @GetMapping("/find/id")
    public String viewFindIdPage(){
        return "/find/find-id";
    }

    @GetMapping("/find/pw")
    public String viewFindPwPage(){
        return "/find/find-pw";
    }

}
