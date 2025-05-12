package kr.plusb3b.games.gamehub.repository.boardrepo;

import kr.plusb3b.games.gamehub.data.board.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
}
