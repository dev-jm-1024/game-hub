package kr.plusb3b.games.gamehub.domain.game.dto;

import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGamesDto {

    //Games 에서 업데이트 할 것
    //approvedAt, isVisible, GameStatus

    public Long gameId;
//    public LocalDateTime approvedAt;
//    public int isVisible;
    public Games.GameStatus gameStatus;
}
