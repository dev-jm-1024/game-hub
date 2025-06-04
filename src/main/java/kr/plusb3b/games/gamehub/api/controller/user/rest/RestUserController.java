package kr.plusb3b.games.gamehub.api.controller.user.rest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.user.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserPrivateRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class RestUserController {

    private final UserPrivateRepository userPrivateRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserRepository userRepo;
    private final JwtProvider jwtProvider;
    private final AccessControlService access;

    public RestUserController(UserPrivateRepository userPrivateRepo, UserAuthRepository userAuthRepo,
                              UserRepository userRepo,JwtProvider jwtProvider, AccessControlService access) {
        this.userPrivateRepo = userPrivateRepo;
        this.userAuthRepo = userAuthRepo;
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.access = access;
    }


    @PatchMapping("/user/{mbId}")
    public ResponseEntity<?> updateUser(@RequestBody RequestUserUpdateDto updateDto,@PathVariable("mbId") Long mbId,
                                        HttpServletRequest request) {

        try {

            //여기다가 Access 메소드
            User user = access.getAuthenticatedUser(request);
            boolean result = mbId.equals(user.getMbId());

            if(!result) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //6.입력받은 객체 조립 시작
            //6.1 user 테이블 - mbNickName, mbProfileUrl, mbStatusMessage
            int userResult = userRepo.updateUserByMbId(
                     updateDto.getMbNickName(),
                    updateDto.getMbProfileUrl(),
                    updateDto.getMbStatusMessage(),
                    mbId
            );

            //6.2 userAuth = authUserId, authPassword, mbId
            int userAuthResult = userAuthRepo.updateUserAuth(
                    updateDto.getAuthUserId(),
                    mbId
            );

            //6.3 USerPrivate priBirth, priEmail, priGender, mbId
            int UserPrivateResult = userPrivateRepo.updateUserPrivate(
                    updateDto.getPriBirth(),
                    updateDto.getPriEmail(),
                    updateDto.getPriGender(),
                    mbId
            );

            int totalResult = userResult + userAuthResult +UserPrivateResult;

            if(totalResult < 3) throw new RuntimeException("update error");
            else{
                return ResponseEntity.status(HttpStatus.OK).body("update success");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error");
    }
}
