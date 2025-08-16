package kr.plusb3b.games.gamehub.domain.game.repository;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GamesFileRepository extends JpaRepository<GamesFile, Long> {

    // ===== 기존 메서드들 =====
    @Modifying
    @Transactional
    @Query("UPDATE GamesFile gf SET gf.gameUrl = :gameUrl, gf.fileStatus = :fileStatus WHERE gf.fileId = :fileId")
    int updateGameUrlAndFileStatusByFileId(
            @Param("gameUrl") String gameUrl,
            @Param("fileStatus") GamesFile.FileStatus fileStatus,
            @Param("fileId") Long fileId
    );

    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.gameId = :gameId")
    List<GamesFile> findByGameGameId(@Param("gameId") Long gameId);

    // ===== 🆕 개선된 메서드들 =====

    /**
     * 게임별 특정 상태의 파일들을 최신순으로 조회
     */
    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.gameId = :gameId AND gf.fileStatus = :fileStatus ORDER BY gf.uploadedAt DESC")
    List<GamesFile> findByGameGameIdAndFileStatusOrderByUploadedAtDesc(@Param("gameId") Long gameId,
                                                                       @Param("fileStatus") GamesFile.FileStatus fileStatus);

    /**
     * 게임별 모든 파일을 최신순으로 조회
     */
    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.gameId = :gameId ORDER BY gf.uploadedAt DESC")
    List<GamesFile> findByGameGameIdOrderByUploadedAtDesc(@Param("gameId") Long gameId);

    /**
     * 게임의 가장 최신 파일 하나만 조회 (Optional 사용)
     */
    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.gameId = :gameId ORDER BY gf.uploadedAt DESC LIMIT 1")
    Optional<GamesFile> findLatestByGameGameId(@Param("gameId") Long gameId);

    /**
     * 게임의 특정 상태 파일 중 가장 최신 것 조회
     */
    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.gameId = :gameId AND gf.fileStatus = :fileStatus ORDER BY gf.uploadedAt DESC LIMIT 1")
    Optional<GamesFile> findLatestByGameGameIdAndFileStatus(@Param("gameId") Long gameId,
                                                            @Param("fileStatus") GamesFile.FileStatus fileStatus);

    /**
     * 사용자의 모든 게임 파일들을 한번에 조회 (batch 처리용)
     */
    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.user.mbId = :mbId ORDER BY gf.uploadedAt DESC")
    List<GamesFile> findAllByUser_MbIdOrderByUploadedAtDesc(@Param("mbId") Long mbId);

    /**
     * 게임에 파일이 존재하는지 확인
     */
    @Query("SELECT COUNT(gf) > 0 FROM GamesFile gf WHERE gf.game.gameId = :gameId")
    boolean existsByGameGameId(@Param("gameId") Long gameId);

    /**
     * 해시값으로 중복 파일 확인 (무결성 검증용)
     */
    @Query("SELECT COUNT(gf) > 0 FROM GamesFile gf WHERE gf.gameHash = :gameHash")
    boolean existsByGameHash(@Param("gameHash") String gameHash);

    /**
     * 사용자의 상태별 파일 개수 조회
     */
    @Query("SELECT gf.fileStatus, COUNT(gf) FROM GamesFile gf WHERE gf.game.user.mbId = :mbId GROUP BY gf.fileStatus")
    List<Object[]> countFilesByStatusForUser(@Param("mbId") Long mbId);
}