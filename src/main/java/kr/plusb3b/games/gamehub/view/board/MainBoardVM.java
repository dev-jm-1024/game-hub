package kr.plusb3b.games.gamehub.view.board;

import kr.plusb3b.games.gamehub.domain.board.entity.*;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;

import java.util.*;

public record MainBoardVM(

        Map<Board, List<Posts>> postTop5,
        Map<Board, List<Games>> gameTop5

) {
}
