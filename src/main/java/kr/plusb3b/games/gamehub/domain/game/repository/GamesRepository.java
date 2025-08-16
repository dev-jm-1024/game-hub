package kr.plusb3b.games.gamehub.domain.game.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GamesRepository extends JpaRepository<Games, Long> {

    // ===== 기존 메서드들 =====
    @Modifying
    @Query("UPDATE Games g SET g.isVisible = :visibility WHERE g.gameId = :gameId")
    int updateVisibilityByGameId(@Param("visibility") Integer visibility, @Param("gameId") Long gameId);

    @Modifying
    @Transactional
    @Query("UPDATE Games g SET g.approvedAt = :approvedAt, g.isVisible = :isVisible, g.status = :status WHERE g.gameId = :gameId")
    int updateApprovalVisibilityAndStatusByGameId(@Param("approvedAt") LocalDateTime approvedAt,
                                                  @Param("isVisible") int isVisible,
                                                  @Param("status") Games.GameStatus status,
                                                  @Param("gameId") Long gameId);

    List<Games> findAllByUser_MbId(Long userMbId);

    // ===== 🆕 개선된 메서드들 =====

    /**
     * 사용자의 특정 상태 게임들을 최신순으로 조회 (DB 레벨 필터링)
     */
    @Query("SELECT g FROM Games g WHERE g.user.mbId = :mbId AND g.status = :status ORDER BY g.createdAt DESC")
    List<Games> findAllByUser_MbIdAndStatusOrderByCreatedAtDesc(@Param("mbId") Long mbId,
                                                                @Param("status") Games.GameStatus status);

    /**
     * 사용자의 모든 게임을 최신순으로 조회
     */
    @Query("SELECT g FROM Games g WHERE g.user.mbId = :mbId ORDER BY g.createdAt DESC")
    List<Games> findAllByUser_MbIdOrderByCreatedAtDesc(@Param("mbId") Long mbId);

    /**
     * 🔥 성능 최적화: 게임과 파일 정보를 한번에 조회 (N+1 문제 해결)
     */
    @Query("SELECT g FROM Games g LEFT JOIN FETCH g.gamesFile WHERE g.user.mbId = :mbId ORDER BY g.createdAt DESC")
    List<Games> findAllByUser_MbIdWithFilesOrderByCreatedAtDesc(@Param("mbId") Long mbId);

    /**
     * 특정 상태의 게임들을 파일 정보와 함께 조회
     */
    @Query("SELECT g FROM Games g LEFT JOIN FETCH g.gamesFile WHERE g.user.mbId = :mbId AND g.status = :status ORDER BY g.createdAt DESC")
    List<Games> findAllByUser_MbIdAndStatusWithFilesOrderByCreatedAtDesc(@Param("mbId") Long mbId,
                                                                         @Param("status") Games.GameStatus status);

    /**
     * 사용자가 업로드한 게임이 있는지 확인 (예외 처리 개선용)
     */
    @Query("SELECT COUNT(g) > 0 FROM Games g WHERE g.user.mbId = :mbId")
    boolean existsByUser_MbId(@Param("mbId") Long mbId);

    /**
     * 사용자의 상태별 게임 개수 조회 (대시보드용)
     */
    @Query("SELECT g.status, COUNT(g) FROM Games g WHERE g.user.mbId = :mbId GROUP BY g.status")
    List<Object[]> countGamesByStatusForUser(@Param("mbId") Long mbId);

    List<Games> findGamesByBoard(Board board);

    List<Games> findGamesByBoard_BoardId(String boardBoardId);
}