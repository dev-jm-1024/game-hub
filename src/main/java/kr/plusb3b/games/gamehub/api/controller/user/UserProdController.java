package kr.plusb3b.games.gamehub.api.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game-hub/prod")
public class UserProdController {

    //업로드 페이지로 이동
    @GetMapping("/upload")
    public String viewUploadPage(){
        return "game/upload/index";
    }

}
