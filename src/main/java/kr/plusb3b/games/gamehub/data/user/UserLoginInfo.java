package kr.plusb3b.games.gamehub.data.user;

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
    @OneToOne
    @JoinColumn(name="mb_id")
    private User user;

    //로그인 기록 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long login_info_id;

    //로그인 시각
    private LocalDateTime login_time;

    //접속 IP주소
    private String ip_address;

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
