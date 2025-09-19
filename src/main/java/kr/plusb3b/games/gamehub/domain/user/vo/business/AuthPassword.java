package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class AuthPassword {

    private String authPassword;

    protected AuthPassword() {
        // JPA 기본 생성자
    }

    private AuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public static AuthPassword of(String authPassword) {
        if (authPassword == null || authPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 공백일 수 없습니다.");
        }

        int passwordSize = authPassword.length();
        if (passwordSize < 10) {
            throw new IllegalArgumentException("비밀번호는 10글자 이상이어야 합니다.");
        }

        // 선택적으로 복잡성 규칙 추가
        if (!authPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("비밀번호에는 최소 1개의 대문자가 포함되어야 합니다.");
        }
        if (!authPassword.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("비밀번호에는 최소 1개의 숫자가 포함되어야 합니다.");
        }
        if (!authPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("비밀번호에는 최소 1개의 특수문자가 포함되어야 합니다.");
        }

        return new AuthPassword(authPassword);
    }

    /** 비밀번호를 그대로 노출하지 않고 마스킹 처리 */
    public String masked() {
        return "********";
    }
}
