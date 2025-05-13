package kr.plusb3b.games.gamehub.repository.boardrepo;

import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
}
