package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import java.util.regex.Pattern;

@Embeddable
@Getter
public class PriEmail {

    private static final int MAX_LENGTH = 70;

    // RFC 5322 간소화 버전 (현실 서비스에서 가장 흔히 사용)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Column(name = "pri_email", nullable = false, unique = true, length = MAX_LENGTH)
    private String priEmail;

    protected PriEmail() {} // JPA 기본 생성자

    private PriEmail(String priEmail) {
        this.priEmail = priEmail;
    }

    public static PriEmail of(String priEmail) {
        if (priEmail == null || priEmail.isBlank()) {
            throw new IllegalArgumentException("이메일은 공백일 수 없습니다.");
        }

        if (priEmail.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("이메일은 " + MAX_LENGTH + "자를 넘으면 안됩니다.");
        }

        // 기본 형식 검사
        if (!EMAIL_PATTERN.matcher(priEmail).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        // @ 위치 확인 (첫 글자 / 마지막 글자에 오면 안 됨)
        int atIndex = priEmail.indexOf("@");
        if (atIndex <= 0 || atIndex == priEmail.length() - 1) {
            throw new IllegalArgumentException("이메일에 올바른 @ 형식이 포함되어야 합니다.");
        }

        // 도메인 부분 점(.) 확인
        String domain = priEmail.substring(atIndex + 1);
        if (!domain.contains(".")) {
            throw new IllegalArgumentException("도메인 부분이 유효하지 않습니다.");
        }

        return new PriEmail(priEmail);
    }

}
