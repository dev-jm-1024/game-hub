package kr.plusb3b.games.gamehub.domain.game.repository;

import kr.plusb3b.games.gamehub.domain.game.entity.GamesFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
import org.springframework.data.repository.query.Param;

public interface GamesFileRepository extends JpaRepository <GamesFile, Long> {

    // GamesFileRepository에 추가
    @Query("SELECT gf FROM GamesFile gf WHERE gf.fileStatus = :status")
    List<GamesFile> findByFileStatus(@Param("status") GamesFile.FileStatus status);
}
