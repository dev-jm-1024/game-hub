package kr.plusb3b.games.gamehub.api.dto.user;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.api.dto.board.Comments;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.api.dto.game.Games;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    private Long mbId;

    private String mbNickname;
    private String mbProfileUrl;
    private String mbStatusMessage;
    private LocalDateTime mbJoinDate;
    private int mbAct;

    //Spring Security에서 ROLE_ 접두사 붙여야 인식 가능
    public enum Role {
        ROLE_USER, ROLE_ADMIN, ROLE_PRODUCER
    }//회원, 관리자, 제작자
    @Enumerated(EnumType.STRING)
    @Column(name = "mb_role")
    private Role mbRole;

    private int mbReportCnt;




    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posts> mbPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<Comments>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserScore> userScores = new ArrayList<>();

    @OneToMany
    private List<UserLoginInfo> userLoginInfo = new ArrayList<>();

    @OneToMany
    private List<Games> games = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAuth userAuth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPrivate userPrivate;


    //여분컬럼1~10
    /*
    private String mb_extra1;
    private String mb_extra2;
    private String mb_extra3;
    private String mb_extra4;
    private String mb_extra5;
    private String mb_extra6;
    private String mb_extra7;
    private String mb_extra8;
    private String mb_extra9;
    private String mb_extra10;
    */
    public User () {}


}
