package kr.plusb3b.games.gamehub.api.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game-hub")
public class GameHubController {

    @GetMapping
    public String showGamePage(){
        return "/main-contents/index"; //바뀔 수 있음. 확정x
    }

    //회원가입 페이지 이동
    @GetMapping("/join")
    public String showJoinPage(){
        return "/join/join-form";
    }

    //로그인 페이지 이동
    @GetMapping("/login")
    public String showLoginPage(){
        return "/login/login-form";
    }
}

