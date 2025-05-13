package kr.plusb3b.games.gamehub.api.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class UserController {


    @GetMapping
    public String showUserSettings() {
        return "usersetting";
    }

    @GetMapping("/game") //내 게임 페이지 안내
    public String showMyGamePage() {
        return "game";
    }

    @GetMapping("/profile") //프로필 페이지 안내
    public String showMyProfilePage() {
        return "profile";
    }

    @GetMapping("/favorites")//즐겨찾기 페이지 안내
    public String showFavoritesPage() {
        return "favorites";
    }
}
