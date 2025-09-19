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
    public ResponseEntity<?> findUserLoginId(@ModelAttribute FindUserDto dto){

        //해당 데이터가 사용자 데이터에 존재하는 지 확인
        Optional<FindUserDto> result = findUserService.findUserAuth(dto);


        return ResponseEntity.status(HttpStatus.OK).body("result");
    }
}
