package kr.plusb3b.games.gamehub.api.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class RequestUserJoinInfoDto {



    //로그인 아이디
    private String auth_user_id;

    //사용자 비밀번호
    private String auth_password;

    //닉네임
    private String mb_nickname;

    //팀 혹은 제작사 이름
    private String team_name;

    //이메일
    private String pri_email;

    //사용자 생년월일
    private LocalDateTime pri_birth;

    //사용자 성별
    private String pri_gender;

    //사용자 역할(권한)
    private User.Role mb_role;

    public RequestUserJoinInfoDto(){}

    public RequestUserJoinInfoDto(String auth_user_id, String auth_password, String mb_nickname,
                                  String team_name, String pri_email, LocalDateTime pri_birth,
                                  String pri_gender, User.Role mb_role) {
        this.auth_user_id = auth_user_id;
        this.auth_password = auth_password;
        this.mb_nickname = mb_nickname;
        this.team_name = team_name;
        this.pri_email = pri_email;
        this.pri_birth = pri_birth;
        this.pri_gender = pri_gender;
        this.mb_role = mb_role;
    }
}
