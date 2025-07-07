package kr.plusb3b.games.gamehub.domain.game.service;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Games, Long> {
}
