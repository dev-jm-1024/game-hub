package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import kr.plusb3b.games.gamehub.domain.game.dto.SummaryGamesDto;
import kr.plusb3b.games.gamehub.view.board.PostGamesListVM;

import java.util.List;

public interface PostGamesListVmService {

    List<SummaryGamesDto> getSummaryGamesByBoardId(String boardId);

    PostGamesListVM getPostGameListVm(String boardId);
}
