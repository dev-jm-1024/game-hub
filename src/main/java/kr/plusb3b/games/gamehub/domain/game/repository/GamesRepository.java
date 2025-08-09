package kr.plusb3b.games.gamehub.domain.game.repository;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GamesRepository extends JpaRepository<Games, Long> {

    // GamesRepository에 추가
    @Query("SELECT g FROM Games g WHERE g.status = 'PENDING_REVIEW' AND g.isVisible = 0")
    List<Games> findPendingReviewGames();

    @Query("SELECT g FROM Games g WHERE g.status = 'ACTIVE' AND g.isVisible = 1")
    List<Games> findActiveGames();

    // 이제 이 쿼리들이 작동합니다
    @Query("SELECT g FROM Games g JOIN FETCH g.gamesFile WHERE g.status = 'PENDING_REVIEW'")
    List<Games> findPendingReviewGamesWithFiles();


    // 🆕 승인된 활성 게임들 (게임 + 파일 정보 한번에 조회)
    @Query("SELECT g FROM Games g JOIN FETCH g.gamesFile gf WHERE g.status = 'ACTIVE' AND g.isVisible = 1 AND gf.fileStatus = 'ACTIVE'")
    List<Games> findActiveGamesWithFiles();

}
