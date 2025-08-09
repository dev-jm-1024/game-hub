package kr.plusb3b.games.gamehub.domain.game.exception;

public class GameUploadException extends RuntimeException {

    // ✅ 메시지만 받는 생성자
    public GameUploadException(String message) {
        super(message);
    }

    // ✅ 메시지 + 원인 예외
    public GameUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
