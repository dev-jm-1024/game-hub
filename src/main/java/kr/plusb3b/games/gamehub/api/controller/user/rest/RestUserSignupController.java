package kr.plusb3b.games.gamehub.api.controller.user.rest;

import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
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
            // prod 필드를 통한 사용자 타입 검증
            String prod = userSignupDto.getProd();
            if (prod == null || (!prod.equals("generalUser") && !prod.equals("producer"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("잘못된 사용자 타입입니다. (generalUser 또는 producer만 허용)");
            }

            // VO는 그대로 사용 (값 변경 안 함)
            UserSignupVO userSignupVO = new UserSignupVO();

            // prod 값에 따라 Role이 설정된 새로운 DTO 생성
            UserSignupDto processedDto = createProcessedDto(userSignupDto, prod);

            // 파일이 있으면 리스트로 변환, 없으면 null
            List<MultipartFile> files = file != null ? List.of(file) : null;

            // 기존 서비스 메서드 시그니처 유지
            userJoinService.signupUser(processedDto, userSignupVO, files);

            // 사용자 타입에 따른 성공 메시지
            String successMessage = "producer".equals(prod) ?
                    "제작사 회원가입이 성공적으로 완료되었습니다." :
                    "일반 회원가입이 성공적으로 완료되었습니다.";

            return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);

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
     * prod 값에 따라 Role이 설정된 새로운 DTO 생성
     * @param originalDto 원본 DTO
     * @param prod 사용자 타입 (generalUser 또는 producer)
     * @return Role이 설정된 새로운 DTO
     */
    private UserSignupDto createProcessedDto(UserSignupDto originalDto, String prod) {
        // prod 값에 따라 Role 결정
        User.Role role = "producer".equals(prod) ? User.Role.ROLE_PRODUCER : User.Role.ROLE_USER;

        return new UserSignupDto(
                originalDto.getAuthUserId(),
                originalDto.getAuthPassword(),
                originalDto.getMbNickname(),
                originalDto.getTeamName(),
                originalDto.getPriEmail(),
                originalDto.getPriBirth(),
                originalDto.getPriGender(),
                originalDto.getMbStatusMessage(),
                role,  // prod에 따라 결정된 Role
                originalDto.getProd()
        );
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