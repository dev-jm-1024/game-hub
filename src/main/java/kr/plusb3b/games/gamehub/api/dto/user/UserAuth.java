package kr.plusb3b.games.gamehub.api.dto.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "user_auth")
public class UserAuth {

    @Id
    private String authUserId; // 로그인 ID

    @OneToOne
    @JoinColumn(name = "mb_Id")
    private User user;

    private String authPassword;

    private LocalDateTime authLastLogin;

    public UserAuth(){}

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
}
