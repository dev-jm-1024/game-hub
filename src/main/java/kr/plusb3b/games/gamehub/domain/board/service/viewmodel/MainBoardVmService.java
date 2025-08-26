package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.view.board.MainBoardVM;

import java.util.List;
import java.util.Map;

public interface MainBoardVmService {

    Map<Board, List<Posts>> getPostsTop5();
    Map<Board, List<Games>> getGamesTop5();
    MainBoardVM getMainBoardVm();
}
