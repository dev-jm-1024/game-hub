package kr.plusb3b.games.gamehub.view.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.game.dto.SummaryGamesDto;

import java.util.List;

public record PostGamesListVM(
        Board board,
        List<SummaryGamesDto> summaryGamesDtoList
) {
}
