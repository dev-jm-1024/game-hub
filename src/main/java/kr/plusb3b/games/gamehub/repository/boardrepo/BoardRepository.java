package kr.plusb3b.games.gamehub.repository.boardrepo;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
