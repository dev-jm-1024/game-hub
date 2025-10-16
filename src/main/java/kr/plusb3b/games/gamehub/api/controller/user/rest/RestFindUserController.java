package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.FindUserDto;
import kr.plusb3b.games.gamehub.domain.user.service.FindUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class RestFindUserController {

    private final FindUserService findUserService;

    public RestFindUserController(FindUserService findUserService) {
        this.findUserService = findUserService;
    }


    @GetMapping("/find/id")
    public ResponseEntity<?> findUserLoginId(@ModelAttribute FindUserDto dto) {

        Optional<String> authUserId = findUserService.getUserAuthId(dto);

        return authUserId
                .map(ResponseEntity::ok)  // 있으면 → 200 OK + 아이디
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("조회된 회원이 없습니다."));  // 없으면 → 404
    }


}
