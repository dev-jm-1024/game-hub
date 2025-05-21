package kr.plusb3b.games.gamehub.api.controller.login.rest;

import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game-hub/api/v1/token")
public class RestJwtManagement {

    private final JwtProvider jwtProvider;

    public RestJwtManagement(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }


    @PostMapping
    public ResponseEntity<?> checkTokenStatus(@RequestHeader(value = "Authorization", required = false) String authHeader){

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
    }

}
