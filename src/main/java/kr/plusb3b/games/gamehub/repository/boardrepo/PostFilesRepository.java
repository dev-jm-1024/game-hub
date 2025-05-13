package kr.plusb3b.games.gamehub.repository.boardrepo;

import kr.plusb3b.games.gamehub.api.dto.board.PostFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFilesRepository extends JpaRepository<PostFiles, Long> {
}
