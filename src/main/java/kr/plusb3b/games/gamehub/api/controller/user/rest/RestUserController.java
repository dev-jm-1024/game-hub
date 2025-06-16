package kr.plusb3b.games.gamehub.api.controller.user.rest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.user.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import kr.plusb3b.games.gamehub.api.dto.user.UserPrivate;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserAuthRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserPrivateRepository;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

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
    public ResponseEntity<?> updateUser(@RequestBody RequestUserUpdateDto updateDto,
                                        @PathVariable("mbId") Long mbId,
                                        HttpServletRequest request) {
        try {
            // 1. JWT 쿠키 추출
            Cookie[] cookies = request.getCookies();
            String jwt = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }

            // 2. JWT 유효성 검사
            if (jwt == null || !jwtProvider.validateToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 3. 사용자 인증 및 권한 검사
            String authUserId = jwtProvider.getUserId(jwt);
            Optional<UserAuth> userAuthOpt = userAuthRepo.findByAuthUserId(authUserId);
            if (userAuthOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
            }

            User user = userAuthOpt.get().getUser();
            if (!Objects.equals(user.getMbId(), mbId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }
            if (user.getMbAct() == 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("탈퇴한 회원입니다.");
            }

            // 4. 기존 정보 조회
            Optional<UserAuth> oldUserAuthOpt = userAuthRepo.findByUser_MbId(mbId);
            Optional<UserPrivate> oldUserPrivateOpt = userPrivateRepo.findUserPrivateByMbId(mbId);
            if (oldUserAuthOpt.isEmpty() || oldUserPrivateOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보가 존재하지 않습니다.");
            }

            UserAuth oldUserAuth = oldUserAuthOpt.get();
            UserPrivate oldUserPrivate = oldUserPrivateOpt.get();

            // 5. 병합된 정보 생성
            RequestUserUpdateDto oldDto = new RequestUserUpdateDto(
                    oldUserAuth.getAuthUserId(),
                    oldUserPrivate.getPriEmail(),
                    oldUserPrivate.getPriBirth(),
                    user.getMbNickname(),
                    user.getMbStatusMessage(),
                    oldUserPrivate.getPriGender(),
                    user.getMbProfileUrl()
            );

            RequestUserUpdateDto mergedDto = mergeUserUpdateDto(oldDto, updateDto);

            // 6. 변경사항 없으면 리턴
            if (mergedDto.equals(oldDto)) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("변경된 내용이 없습니다.");
            }

            // 7. DB 업데이트
            int userResult = userRepo.updateUserByMbId(
                    mergedDto.getMbNickName(),
                    mergedDto.getMbProfileUrl(),
                    mergedDto.getMbStatusMessage(),
                    mbId
            );

            int authResult = userAuthRepo.updateUserAuth(
                    mergedDto.getAuthUserId(),
                    mbId
            );

            int privateResult = userPrivateRepo.updateUserPrivate(
                    mergedDto.getPriBirth(),
                    mergedDto.getPriEmail(),
                    mergedDto.getPriGender(),
                    mbId
            );

            // 8. 처리 결과
            if (userResult >= 0 && authResult >= 0 && privateResult >= 0) {
                return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일부 정보 수정에 실패했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    //아이디 중복 확인해주는 메소드
    @GetMapping("/user/check-id")
    public ResponseEntity<?> checkDuplicateUserId(@RequestParam String userId){

        if(userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<UserAuth> userAuthOpt = userAuthRepo.findByAuthUserId(userId);


        if(userAuthOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 아이디 입니다!");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("존재하는 아이디 입니다!");
        }

    }


    private static RequestUserUpdateDto mergeUserUpdateDto(
            RequestUserUpdateDto oldDto,
            RequestUserUpdateDto newDto) {

        return new RequestUserUpdateDto(
                newDto.getAuthUserId() != null ? newDto.getAuthUserId() : oldDto.getAuthUserId(),
                newDto.getPriEmail() != null ? newDto.getPriEmail() : oldDto.getPriEmail(),
                newDto.getPriBirth() != null ? newDto.getPriBirth() : oldDto.getPriBirth(),
                newDto.getMbNickName() != null ? newDto.getMbNickName() : oldDto.getMbNickName(),
                newDto.getMbStatusMessage() != null ? newDto.getMbStatusMessage() : oldDto.getMbStatusMessage(),
                newDto.getPriGender() != null ? newDto.getPriGender() : oldDto.getPriGender(),
                newDto.getMbProfileUrl() != null ? newDto.getMbProfileUrl() : oldDto.getMbProfileUrl()
        );
    }



}
