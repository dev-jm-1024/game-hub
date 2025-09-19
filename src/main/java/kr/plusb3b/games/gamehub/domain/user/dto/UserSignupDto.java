package kr.plusb3b.games.gamehub.domain.user.dto;

import jakarta.validation.constraints.*;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserSignupDto {

    // 로그인 아이디
    @NotBlank(message = "로그인 아이디는 공백일 수 없습니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하여야 합니다.")
    private String authUserId;

    // 사용자 비밀번호
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String authPassword;

    // 닉네임
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
    private String mbNickname;

    // 팀 혹은 제작사 이름 : 일반 사용자는 NULL 값
    @Size(max = 50, message = "팀 이름은 50자 이하여야 합니다.")
    private String teamName;

    // 이메일
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @Size(max = 70, message = "이메일은 70자 이하여야 합니다.")
    private String priEmail;

    // 사용자 생년월일
    @NotNull(message = "생년월일은 필수 입력값입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate priBirth;

    // 사용자 성별
    @NotBlank(message = "성별은 공백일 수 없습니다.")
    @Pattern(regexp = "^(남|여|기타)$", message = "성별은 '남', '여', '기타' 중 하나여야 합니다.")
    private String priGender;

    // 사용자 상태 메시지
    @Size(max = 100, message = "상태 메시지는 100자 이하여야 합니다.")
    private String mbStatusMessage;

    // 사용자 역할(권한)
    @NotNull(message = "사용자 권한은 필수 입력값입니다.")
    private User.Role mbRole;

    // 일반 사용자 혹은 제작사 구분 필드 -- 실제 DB 저장 X
    @NotBlank(message = "사용자 구분은 공백일 수 없습니다.")
    @Pattern(regexp = "^(USER|PRODUCER)$", message = "사용자 구분은 'USER' 또는 'PRODUCER'여야 합니다.")
    private String prod;
}
