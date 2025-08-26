package kr.plusb3b.games.gamehub.view.board;

import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;

import java.util.List;

public record PostListVM(
        Board board,
        List<SummaryPostDto> summaryPostDto
) {
}
