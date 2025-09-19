package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDate;

@Embeddable
@Getter
public class PriBirth {

    private LocalDate priBirth;

    protected PriBirth() {} // JPA 기본 생성자

    private PriBirth(LocalDate priBirth) {
        this.priBirth = priBirth;
    }

    public static PriBirth of(LocalDate priBirth) {
        if (priBirth == null) {
            throw new IllegalArgumentException("생년월일은 공백일 수 없습니다.");
        }

        // 미래 날짜 방지
        if (priBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("생년월일은 미래일 수 없습니다.");
        }

        // 최소 연령 제한 (예: 만 14세 이상만 가입 가능)
        LocalDate minAllowedDate = LocalDate.now().minusYears(14);
        if (priBirth.isAfter(minAllowedDate)) {
            throw new IllegalArgumentException("만 14세 이상만 가입할 수 있습니다.");
        }

        // 비현실적인 과거 제한 (1900년 이전은 허용하지 않음)
        if (priBirth.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new IllegalArgumentException("생년월일이 1900년 이전일 수는 없습니다.");
        }

        return new PriBirth(priBirth);
    }

    @Override
    public String toString() {
        return priBirth + "";
    }
}
