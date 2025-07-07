package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostFilesRepository extends JpaRepository<PostFiles, Long> {

    Optional<PostFiles> findPostFilesByPost_PostId(Long postPostId);
}
