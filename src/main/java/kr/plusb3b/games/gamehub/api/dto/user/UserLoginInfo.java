package kr.plusb3b.games.gamehub.api.dto.user;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long loginInfoId;

    //로그인 시각
    private LocalDateTime loginTime;

    //접속 IP주소
    private String ipAddress;

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

    public UserLoginInfo() {}

}
