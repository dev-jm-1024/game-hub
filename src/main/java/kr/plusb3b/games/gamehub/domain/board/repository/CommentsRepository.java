package kr.plusb3b.games.gamehub.domain.board.repository;


import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
}
