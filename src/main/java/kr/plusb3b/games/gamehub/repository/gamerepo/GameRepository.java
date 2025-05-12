package kr.plusb3b.games.gamehub.repository.gamerepo;

import kr.plusb3b.games.gamehub.data.game.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Games, Long> {
}
