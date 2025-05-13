package kr.plusb3b.games.gamehub.api.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String indexRedirect() {
        return "redirect:/game-hub";
    }


}
