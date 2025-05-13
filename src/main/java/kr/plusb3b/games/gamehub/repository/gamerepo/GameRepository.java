package kr.plusb3b.games.gamehub.repository.gamerepo;

import kr.plusb3b.games.gamehub.api.dto.game.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Games, Long> {
}
