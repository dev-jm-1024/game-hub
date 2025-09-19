package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class AuthUserId {

    @Column(name = "auth_user_id", nullable = false, unique = true, length = 50)
    private final String authUserId;

    // JPA 기본 생성자
    protected AuthUserId() {
        this.authUserId = null;
    }

    private AuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public static AuthUserId of(String authUserId) {
        if (authUserId == null || authUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("아이디가 누락되었습니다");
        }
        if (authUserId.length() < 4 || authUserId.length() > 20) {
            throw new IllegalArgumentException("아이디는 4~20자여야 합니다.");
        }
        if (!authUserId.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("아이디는 영문, 숫자, 밑줄(_)만 허용됩니다.");
        }
        return new AuthUserId(authUserId.trim());
    }

    @Override
    public String toString() {
        return authUserId;
    }
}
