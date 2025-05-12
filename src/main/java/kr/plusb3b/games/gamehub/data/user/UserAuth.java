package kr.plusb3b.games.gamehub.data.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "user_auth")
public class UserAuth {

    //외래키
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId // auth_mb_id = user.mb_id
    @JoinColumn(name="auth_mb_id")
    private User user;

    //고유키
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long auth_user_id;

    //사용자 비밀번호 추후 해시화 예정
    private String auth_password;

    //사용자 마지막 로그인 날짜
    private LocalDateTime auth_last_login;

    //여번컬럼
    /*
    private String auth_extra1;
    private String auth_extra2;
    private String auth_extra3;
    private String auth_extra4;
    private String auth_extra5;
    private String auth_extra6;
    private String auth_extra7;
    private String auth_extra8;
    private String auth_extra9;
    private String auth_extra10;
    */
    public UserAuth(){}

}
