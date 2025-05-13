package kr.plusb3b.games.gamehub.api.controller.upload;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/upload")
public class UploadController {

    @GetMapping
    public String showUploadPage(){
        return "upload"; //변경될 수 있음
    }
}
