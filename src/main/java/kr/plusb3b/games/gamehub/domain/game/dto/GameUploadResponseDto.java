package kr.plusb3b.games.gamehub.domain.game.dto;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class GameUploadResponseDto {
    private Long gameId;
    private String gameName;
    private String teamName;
    private String status;
    private String fileUrl;
    private java.time.LocalDateTime uploadedAt;
    private String message;
    private boolean success = true;
}