package kr.plusb3b.games.gamehub.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDto {

    private String boardId;
    private Long gameId;
    private String gameName;
    private String gameDescription;
    private String teamName;
    private String specs;
    private String gameVersion;
    private String genre;
    private String platform;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;

    // 게임 파일 정보
    private String gameUrl;
    private String originalFilename;
    private Long fileSize;
}