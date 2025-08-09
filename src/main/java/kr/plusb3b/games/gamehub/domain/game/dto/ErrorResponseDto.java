package kr.plusb3b.games.gamehub.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private boolean success = false;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;

    public static ErrorResponseDto of(String message) {
        return ErrorResponseDto.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponseDto of(String message, String errorCode) {
        return ErrorResponseDto.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}