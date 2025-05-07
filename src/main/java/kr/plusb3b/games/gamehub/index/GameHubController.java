package kr.plusb3b.games.gamehub.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game-hub")
public class GameHubController {

    @GetMapping
    public String showGameHubMainPage() {
        return "main-contents/index";
    }
}
