package kr.plusb3b.games.gamehub.api.controller.board.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.RequestCommentDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class RestCommentController {

    private final AccessControlService access;

    public RestCommentController(AccessControlService access) {
        this.access = access;
    }

    @PostMapping
    public ResponseEntity<?> submitComment(@RequestBody RequestCommentDto requestCommentDto, HttpServletRequest request) {


        try{

            //로그인한 사용자인 지 검사
            User user = access.getAuthenticatedUser(request);
            if(user == null ) throw new RuntimeException("알 수 없는 오류");


        }catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }
}
