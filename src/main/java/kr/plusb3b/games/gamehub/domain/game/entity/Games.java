package kr.plusb3b.games.gamehub.domain.game.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Games {

    // 게임 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "game_id", nullable = false, unique = true)
    private Long gameId;

    // 게시물 아이디
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    // 올린 사람 아이디
    @ManyToOne
    @JoinColumn(name = "mb_id")
    private User user;

    // 게임 이름
    @Column(name = "game_name", nullable = false, length = 200)
    private String gameName;

    // 게임 소개
    @Column(name = "game_description", columnDefinition = "TEXT")
    private String gameDescription;

    // 제작사 또는 팀 이름
    @Column(name = "team_name", nullable = false, length = 100)
    private String teamName;

    // 권장사양
    @Column(name = "specs", length = 500)
    private String specs;

    // 게임 등록일
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 게임 버전
    @Column(name = "game_version", length = 50)
    private String gameVersion;

    // 게임 장르
    @Column(name = "genre", nullable = false, length = 50)
    private String genre;

    // 게임 지원 플랫폼 (모바일 혹은 PC)
    @Column(name = "platform", nullable = false, length = 20)
    private String platform;

    // 게임 상태 enum
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GameStatus status;

    // 승인 날짜
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 게임 노출 여부 (1: 노출, 0: 숨김)
    @Column(name = "is_visible", nullable = false)
    private int isVisible;

    // 🆕 GamesFile과의 양방향 관계 설정
    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GamesFile gamesFile;

    // GameStatus enum 정의
    public enum GameStatus {
        PENDING_REVIEW,    // 승인 대기
        UNDER_REVIEW,      // 검토 중
        ACTIVE,            // 활성
        REJECTED,          // 승인 거절
        SUSPENDED,         // 일시 중단
        DEACTIVATED,       // 서비스 종료
        UPLOAD_FAILED      // 업로드 실패
    }

    public Games() {}


    public Games(Board board, User user, String gameName, String gameDescription,
                 String teamName, String specs, LocalDateTime createdAt, String gameVersion,
                 String genre, String platform, GameStatus status, LocalDateTime approvedAt, int isVisible) {
        this.board = board;
        this.user = user;
        this.gameName = gameName;
        this.gameDescription = gameDescription;
        this.teamName = teamName;
        this.specs = specs;
        this.createdAt = createdAt;
        this.gameVersion = gameVersion;
        this.genre = genre;
        this.platform = platform;
        this.status = status;
        this.approvedAt = approvedAt;
        this.isVisible = isVisible;
    }

    public boolean isStatusPendingReview () {
        return this.status == GameStatus.PENDING_REVIEW;
    }

    public boolean isStatusActive(){
        return this.status == GameStatus.ACTIVE;
    }

    public boolean isVisible() {
        return this.isVisible == 1;
    }

    public boolean isNotVisible() {
        return this.isVisible == 0;
    }

}