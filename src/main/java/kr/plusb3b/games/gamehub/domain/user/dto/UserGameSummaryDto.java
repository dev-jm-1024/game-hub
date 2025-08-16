package kr.plusb3b.games.gamehub.domain.user.dto;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자의 게임 업로드 상태 확인용 순수 데이터 전달 객체
 * 비즈니스 로직, UI 로직, 포맷팅 로직은 포함하지 않음
 */
@Getter
@Setter
public class UserGameSummaryDto {

    // ===== 게임 기본 정보 =====
    private Long gameId;
    private String gameName;
    private String teamName;
    private LocalDateTime createdAt;
    private Games.GameStatus status;

    // ===== 게임 세부 정보 =====
    private Long userId;
    private String gameDescription;
    private String specs;
    private String gameVersion;
    private String genre;
    private String platform;
    private LocalDateTime approvedAt;
    private boolean isVisible;

    // ===== 파일 정보 =====
    private String originalFilename;
    private Long fileSize;
    private String gameUrl;
    private String gameHash;
    private LocalDateTime uploadedAt;
    private GamesFile.FileStatus fileStatus;

    // ===== 생성자들 =====
    public UserGameSummaryDto() {}

    /**
     * Games와 GamesFile 엔티티로부터 순수 데이터만 복사
     */
    public UserGameSummaryDto(Games game, GamesFile gamesFile) {
        // 게임 정보 복사
        this.gameId = game.getGameId();
        this.gameName = game.getGameName();
        this.teamName = game.getTeamName();
        this.createdAt = game.getCreatedAt();
        this.status = game.getStatus();
        this.userId = game.getUser().getMbId();
        this.gameDescription = game.getGameDescription();
        this.specs = game.getSpecs();
        this.gameVersion = game.getGameVersion();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.approvedAt = game.getApprovedAt();
        this.isVisible = game.isVisible();

        // 파일 정보 복사 (null 체크)
        if (gamesFile != null) {
            this.originalFilename = gamesFile.getOriginalFilename();
            this.fileSize = gamesFile.getFileSize();
            this.gameUrl = gamesFile.getGameUrl();
            this.gameHash = gamesFile.getGameHash();
            this.uploadedAt = gamesFile.getUploadedAt();
            this.fileStatus = gamesFile.getFileStatus();
        }
    }
}