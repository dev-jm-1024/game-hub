package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class RestUserSignupController {

    private final UserJoinService userJoinService;

    public RestUserSignupController(UserJoinService userJoinService) {
        this.userJoinService = userJoinService;
    }

    /**
     * 회원가입 처리 - multipart/form-data만 사용
     * @param userSignupDto 회원가입 데이터 (JSON 형태로 전송)
     * @param file 프로필 사진 파일 (선택사항)
     * @return 회원가입 결과
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
            @RequestPart("userSignupDto") UserSignupDto userSignupDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            UserSignupVO userSignupVO = new UserSignupVO();

            // 파일이 있으면 리스트로 변환, 없으면 null
            List<MultipartFile> files = file != null ? List.of(file) : null;

            userJoinService.signupUser(userSignupDto, userSignupVO, files);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("회원가입이 성공적으로 완료되었습니다.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 요청: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 존재하는 사용자입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 ID 중복 검사
     * @param userId 검사할 사용자 ID
     * @return 중복 검사 결과
     */
    @GetMapping("/check-id")
    public ResponseEntity<?> checkUserIdDuplicate(@RequestParam("userId") String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("아이디는 공백일 수 없습니다.");
        }

        boolean isDuplicated = userJoinService.isDuplicatedLoginId(userId);

        if (isDuplicated) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("이미 존재하는 아이디입니다.");
        }

        return ResponseEntity.ok("사용 가능한 아이디입니다.");
    }

    /**
     * 이메일 중복 검사
     * @param email 검사할 이메일
     * @return 중복 검사 결과
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이메일은 공백일 수 없습니다.");
        }

        boolean isDuplicated = userJoinService.checkDistinctEmail(email);

        if (isDuplicated) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("이미 존재하는 이메일입니다.");
        }

        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
}