package kr.plusb3b.games.gamehub.api.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
public class RequestUserJoinInfoDto {



    //로그인 아이디
    private String authUserId;

    //사용자 비밀번호
    private String authPassword;

    //닉네임
    private String mbNickname;

    //팀 혹은 제작사 이름
    private String teamName;

    //이메일
    private String priEmail;

    //사용자 생년월일
    private LocalDate priBirth;

    //사용자 성별
    private String priGender;

    //사용자 역할(권한)
    private User.Role mbRole;

    public RequestUserJoinInfoDto(){}

    public RequestUserJoinInfoDto(String authUserId, String authPassword, String mbNickname,
                                  String teamName, String priEmail, LocalDate priBirth,
                                  String priGender, User.Role mbRole) {
        this.authUserId = authUserId;
        this.authPassword = authPassword;
        this.mbNickname = mbNickname;
        this.teamName = teamName;
        this.priEmail = priEmail;
        this.priBirth = priBirth;
        this.priGender = priGender;
        this.mbRole = mbRole;
    }
}
