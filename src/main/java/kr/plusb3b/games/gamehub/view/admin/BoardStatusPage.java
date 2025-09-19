package kr.plusb3b.games.gamehub.view.admin;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;

import java.util.List;
import java.util.Map;

public record BoardStatusPage(

        List<Board> boardList,
        Map<String, String> boardApiPaths
) {
}
