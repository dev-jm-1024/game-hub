package kr.plusb3b.games.gamehub.api.controller.exception;

import jakarta.persistence.EntityNotFoundException;
import kr.plusb3b.games.gamehub.domain.game.dto.ErrorResponseDto;
import kr.plusb3b.games.gamehub.domain.game.exception.GameUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.reflections.Reflections.log;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 요청 (유효성 검사 실패, 잘못된 파라미터 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage()); // 400
    }

    // 엔티티를 찾을 수 없는 경우 (ex. 게시판, 사용자 없음)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage()); // 404
    }

    // 예상하지 못한 모든 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        // 디버깅을 위해 로그를 찍는 게 권장됩니다
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다."); // 500
    }

    @ExceptionHandler(GameUploadException.class)
    public ResponseEntity<?> handleGameUploadFail(GameUploadException e){

        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.of(e.getMessage(), "UPLOAD_ERROR"));

    }
}
