package kr.plusb3b.games.gamehub.api.dto.user;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.api.dto.board.Comments;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
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
    private Long mb_id;

    private String mb_nickname;
    private String mb_profile_url;
    private String mb_status_message;
    private LocalDateTime mb_join_date;
    private int mb_act;

    public enum Role {
        USER, ADMIN, MODERATOR
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "mb_role")
    private Role mb_role;

    private int mb_report_cnt;




    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posts> mb_posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<Comments>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserScore> user_scores = new ArrayList<>();

    @OneToMany
    private List<UserLoginInfo> user_login_info = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAuth user_auth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPrivate user_private;

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
