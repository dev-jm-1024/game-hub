package kr.plusb3b.games.gamehub.domain.game.repository;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GamesFileRepository extends JpaRepository <GamesFile, Long> {



    @Modifying
    @Transactional
    @Query("UPDATE GamesFile gf SET gf.gameUrl = :gameUrl, gf.fileStatus = :fileStatus WHERE gf.fileId = :fileId")
    int updateGameUrlAndFileStatusByFileId(
            @Param("gameUrl") String gameUrl,
            @Param("fileStatus") GamesFile.FileStatus fileStatus,
            @Param("fileId") Long fileId
    );




//    @Query("SELECT gf FROM GamesFile gf WHERE gf.fileStatus = :status")
//    List<GamesFile> findByFileStatus(@Param("status") GamesFile.FileStatus status);
//
//    @Modifying
//    @Query("UPDATE GamesFile gf SET gf.fileStatus = :status WHERE gf.fileId = :fileId")
//    int updateFileStatusByFileId(@Param("status") GamesFile.FileStatus status, @Param("fileId") Long fileId);
//
//    @Query("SELECT gf FROM GamesFile gf WHERE gf.game.gameId = :gameId")
//    GamesFile findByGameGameId(@Param("gameId") Long gameId);
//
//    @Modifying
//    @Query("UPDATE Games g SET g.isVisible = :visibility WHERE g.gameId = :gameId")
//    int updateVisibilityByGameId(@Param("visibility") Integer visibility, @Param("gameId") Long gameId);

}
