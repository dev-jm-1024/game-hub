package kr.plusb3b.games.gamehub.domain.user.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.user.vo.business.MbNickName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @Column(name = "mb_id")
    private Long mbId;

    @Embedded
    @AttributeOverride(name = "mbNickName",
            column = @Column(name = "mb_nickname", nullable = false))
    private MbNickName mbNickName;
    private String mbProfileUrl;
    private String mbStatusMessage;
    private LocalDateTime mbJoinDate;
    private int mbAct;

    //Spring Security 에서 ROLE_ 접두사 붙여야 인식 가능
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAuth userAuth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPrivate userPrivate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPostsReaction> postReactions = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommentsReaction> userCommentsReactionList = new ArrayList<>();



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

    public User(int mbReportCnt, Long mbId, MbNickName mbNickName, String mbProfileUrl,
                String mbStatusMessage, LocalDateTime mbJoinDate, int mbAct, Role mbRole) {

        this.mbReportCnt = mbReportCnt;
        this.mbId = mbId;
        this.mbNickName = mbNickName;
        this.mbProfileUrl = mbProfileUrl;
        this.mbStatusMessage = mbStatusMessage;
        this.mbJoinDate = mbJoinDate;
        this.mbAct = mbAct;
        this.mbRole = mbRole;

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return mbAct == user.mbAct &&
                mbReportCnt == user.mbReportCnt &&
                Objects.equals(mbId, user.mbId) &&
                Objects.equals(mbNickName, user.mbNickName) &&
                Objects.equals(mbProfileUrl, user.mbProfileUrl) &&
                Objects.equals(mbStatusMessage, user.mbStatusMessage) &&
                Objects.equals(mbJoinDate, user.mbJoinDate) &&
                mbRole == user.mbRole &&
                Objects.equals(mbPosts, user.mbPosts) &&
                Objects.equals(comments, user.comments) &&
                Objects.equals(userScores, user.userScores) &&
                Objects.equals(userLoginInfo, user.userLoginInfo) &&
                Objects.equals(games, user.games) &&
                Objects.equals(userAuth, user.userAuth) &&
                Objects.equals(userPrivate, user.userPrivate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mbId, mbNickName, mbProfileUrl, mbStatusMessage, mbJoinDate, mbAct, mbRole, mbReportCnt, mbPosts, comments, userScores, userLoginInfo, games, userAuth, userPrivate);
    }

    //닉네임 변경
    public void changeMbNickName(MbNickName mbNickName) {
        this.mbNickName = mbNickName;
    }

    //프로필 사진 변경
    public void changeMbProfileUrl(String mbProfileUrl) {
        this.mbProfileUrl = mbProfileUrl;
    }

    //상태 메세지 변경
    public void changeMbStatusMessage(String mbStatusMessage) {
        this.mbStatusMessage = mbStatusMessage;
    }

    //계정 활성화

    //계정의 상태가 활성화인지?
    public boolean isActivateUser(){
        return this.mbAct == 1;
    }

    public void deactivateUser(){
        this.mbAct = 0;
    }

    public void activateUser(){
        this.mbAct = 1;
    }

    //신고 횟수 증가 로직
    public void increaseReportCnt(){
        this.mbReportCnt++;
    }

    //prod 값을 통해 role 구분

    // User.java의 prodDifferentiate 메서드 수정
    public Role prodDifferentiate(String userProd){
        if(userProd.equals("generalUser")) return User.Role.ROLE_USER;
        else return User.Role.ROLE_PRODUCER; // 🔄 ROLE_ADMIN → ROLE_PRODUCER
    }
}
