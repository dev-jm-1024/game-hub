package kr.plusb3b.games.gamehub.api.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/games")
public class GameHubController {

    @GetMapping
    public String showGamePage(){
        return "game"; //바뀔 수 있음. 확정x
    }
}

