package kr.plusb3b.games.gamehub.domain.board.vo.business;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostTitle {

    @Column(name = "post_title", length = 200, nullable = false)
    private String value;

    protected PostTitle() {} // JPA 기본 생성자

    private PostTitle(String value) {
        this.value = value;
    }

    // ✅ static 팩토리 메서드에서 비즈니스 로직 실행
    public static PostTitle of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("제목은 비어 있을 수 없습니다.");
        }
        String trimmed = raw.trim();
        if (trimmed.length() > 200) {
            throw new IllegalArgumentException("제목은 200자를 넘을 수 없습니다.");
        }
        return new PostTitle(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
