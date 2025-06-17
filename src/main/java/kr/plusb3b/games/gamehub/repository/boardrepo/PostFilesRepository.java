package kr.plusb3b.games.gamehub.repository.boardrepo;

import kr.plusb3b.games.gamehub.api.dto.board.PostFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostFilesRepository extends JpaRepository<PostFiles, Long> {

    Optional<PostFiles> findPostFilesByPost_PostId(Long postPostId);
}
