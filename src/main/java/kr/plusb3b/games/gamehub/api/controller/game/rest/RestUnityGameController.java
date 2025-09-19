package kr.plusb3b.games.gamehub.api.controller.game.rest;

import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game-hub/api/v1/games")
public class RestUnityGameController {

    private final AccessControlService access;

    public RestUnityGameController(AccessControlService access) {
        this.access = access;
    }

    @GetMapping("/play/user")
    public ResponseEntity<?> getUserByUnity(){

        //쿠키에서 mbId 값 추출

        return ResponseEntity.ok("play-user");
    }


}
