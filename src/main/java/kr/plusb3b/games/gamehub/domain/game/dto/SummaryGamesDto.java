package kr.plusb3b.games.gamehub.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryGamesDto {

    String boardId;
    Long gameId;
    String teamName;
    String gameName;
    LocalDateTime approvedAt;

}
