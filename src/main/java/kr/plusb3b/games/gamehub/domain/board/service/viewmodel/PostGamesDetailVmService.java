package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.view.board.PostDetailVM;
import kr.plusb3b.games.gamehub.view.board.PostGamesDetailVM;

public interface PostGamesDetailVmService {

    PostGamesDetailVM getPostGamesDetailVm(HttpServletRequest request, String boardId, Long gameId);

}
