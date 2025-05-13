package kr.plusb3b.games.gamehub.repository.boardrepo;


import kr.plusb3b.games.gamehub.api.dto.board.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
}
