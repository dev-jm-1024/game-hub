package kr.plusb3b.games.gamehub.domain.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "games_file")
public class GamesFile {

    // 파일 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fileId;

    // 게임과 1:1 관계
    @OneToOne
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private Games game;

    // 게임 파일 무결성 확인용 해시값
    @Column(name = "game_hash", unique = true, length = 64, nullable = false)
    private String gameHash;

    // 원본 파일명
    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    // 현재 파일 URL (temp, activate, deactivate 중 하나)
    @Column(name = "game_url", length = 500)
    private String gameUrl;

    // 파일 크기 (바이트)
    @Column(name = "file_size")
    private Long fileSize;

    // 파일 업로드 날짜
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    // 파일 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "file_status", nullable = false, length = 20)
    private FileStatus fileStatus = FileStatus.TEMP;

    // 파일 상태 enum
    public enum FileStatus {
        TEMP,        // temp-game 폴더
        ACTIVE,      // activate-game 폴더
        DEACTIVATED  // deactivate-game 폴더
    }

    public GamesFile() {}

    public GamesFile(Games game, String gameHash, String originalFilename,
                     String gameUrl, Long fileSize, LocalDateTime uploadedAt,
                     FileStatus fileStatus) {
        this.game = game;
        this.gameHash = gameHash;
        this.originalFilename = originalFilename;
        this.gameUrl = gameUrl;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.fileStatus = fileStatus;
    }
}