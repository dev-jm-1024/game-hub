package kr.plusb3b.games.gamehub.domain.user.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthUserId;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthPassword;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_auth")
public class UserAuth {

    @Id
    private AuthUserId authUserId; // 로그인 ID

    @OneToOne
    @JoinColumn(name = "mb_id")
    private User user;

    @Embedded
    private AuthPassword authPassword;

    private LocalDateTime authLastLogin;

    public UserAuth(){}

    // AuthUserId VO를 받는 생성자
    public UserAuth(AuthUserId authUserId, User user, AuthPassword authPassword, LocalDateTime authLastLogin) {
        this.authUserId = authUserId;
        this.user = user;
        this.authPassword = authPassword;
        this.authLastLogin = authLastLogin;
    }

    // String을 받는 편의 생성자
    public UserAuth(String authUserId, User user, String authPassword, LocalDateTime authLastLogin) {
        this.authUserId = AuthUserId.of(authUserId);
        this.user = user;
        this.authPassword = AuthPassword.of(authPassword);
        this.authLastLogin = authLastLogin;
    }

    // 지난 로그인 시간 변경
    public void changeAuthLastLogin(LocalDateTime lastLogin){
        this.authLastLogin = lastLogin;
    }

    // 비밀번호 변경
    public void changePassword(String password){
        this.authPassword = AuthPassword.of(password);
    }

    // 비밀번호 변경 (VO 직접 사용)
    public void changePassword(AuthPassword password){
        this.authPassword = password;
    }

    // 로그인 ID와 비밀번호 검증
    public boolean isValidLoginCredentials(String authUserId, String authPassword){
        if(authUserId == null || authPassword == null){
            return false;
        }
        return true;
    }

    // AuthUserId 값 반환 (편의 메소드)
    public String getAuthUserIdValue() {
        return this.authUserId != null ? this.authUserId.getAuthUserId() : null;
    }
}