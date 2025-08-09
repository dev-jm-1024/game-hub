package kr.plusb3b.games.gamehub.domain.game.vo;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import lombok.Getter;

import java.time.LocalDateTime;

import static kr.plusb3b.games.gamehub.domain.game.entity.Games.GameStatus.PENDING_REVIEW;

@Getter
public final class GamesVO {

    private final LocalDateTime approvedAt = null;
    private final Games.GameStatus gameStatus = PENDING_REVIEW;
    private final int isVisible = 0;
}
