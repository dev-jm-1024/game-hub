package kr.plusb3b.games.gamehub.domain.game.repository;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface GamesRepository extends JpaRepository<Games, Long> {



//    // GamesRepository.java에 추가
//    @Query("SELECT g FROM Games g LEFT JOIN FETCH g.gamesFile WHERE g.status = :status")
//    List<Games> findGamesByStatusWithFiles(@Param("status") Games.GameStatus status);
//
//    // 이제 이 쿼리들이 작동합니다
//    @Query("SELECT g FROM Games g JOIN FETCH g.gamesFile WHERE g.status = 'PENDING_REVIEW'")
//    List<Games> findPendingReviewGamesWithFiles();
//
//    // 🆕 승인된 활성 게임들 (게임 + 파일 정보 한번에 조회)
//    @Query("SELECT g FROM Games g JOIN FETCH g.gamesFile gf WHERE g.status = 'ACTIVE' AND g.isVisible = 1 AND gf.fileStatus = 'ACTIVE'")
//    List<Games> findActiveGamesWithFiles();



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


}
