package kr.plusb3b.games.gamehub.api.dto.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="user_login_info")
public class UserLoginInfo {

    //외래키
    @ManyToOne
    @JoinColumn(name="mbId")
    private User user;

    //로그인 기록 고유 아이디
    @Id
    private Long loginInfoId;

    //로그인 시각
    @NotNull
    private LocalDateTime loginTime;

    //접속 IP주소
    @NotNull
    private String ipAddress;

    public UserLoginInfo() {}
    //여분컬럼
    /*
    private String login_extra1;
    private String login_extra2;
    private String login_extra3;
    private String login_extra4;
    private String login_extra5;
    private String login_extra6;
    private String login_extra7;
    private String login_extra8;
    private String login_extra9;
    private String login_extra10;

     */

}
