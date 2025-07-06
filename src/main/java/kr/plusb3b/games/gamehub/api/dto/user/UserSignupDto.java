package kr.plusb3b.games.gamehub.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserSignupDto {

    //로그인 아이디
    private String authUserId;

    //사용자 비밀번호
    private String authPassword;

    //닉네임
    private String mbNickname;

    //팀 혹은 제작사 이름 : 일반 사용자는 NULL 값
    private String teamName;

    //이메일
    private String priEmail;

    //사용자 생년월일
    private LocalDate priBirth;

    //사용자 성별
    private String priGender;

    //사용자 역할(권한)
    private User.Role mbRole;

    //일반 사용자 혹은 제작사 구분 필드 -- 실제 DB 저장 X
    private String prod;

}
