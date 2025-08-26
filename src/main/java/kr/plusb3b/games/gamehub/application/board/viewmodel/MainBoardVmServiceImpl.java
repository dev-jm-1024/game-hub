package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.MainBoardVmService;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import kr.plusb3b.games.gamehub.view.board.MainBoardVM;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MainBoardVmServiceImpl implements MainBoardVmService {

    private final BoardService boardService;
    private final PostsService postsService;
    private final GameMetadataService gameMetadataService;

    public MainBoardVmServiceImpl(BoardService boardService, PostsService postsService,
                                  GameMetadataService gameMetadataService) {
        this.boardService = boardService;
        this.postsService = postsService;
        this.gameMetadataService = gameMetadataService;
    }

    @Override
    public MainBoardVM getMainBoardVm() {

        return new MainBoardVM(
                getPostsTop5(),
                getGamesTop5()
        );
    }

    @Override
    public Map<Board, List<Posts>> getPostsTop5() {
        Map<Board, List<Posts>> postsByBoard = boardService.loadTop5LatestPostsByBoard();

        if (postsByBoard.isEmpty()) {
            return Collections.emptyMap(); // ← 이거!
        }

        return postsByBoard;
    }


    @Override
    public Map<Board, List<Games>> getGamesTop5() {
        Map<Board, List<Games>> gamesByBoard = boardService.loadTop5LatestGamesByBoard();

        if (gamesByBoard.isEmpty()) return Collections.emptyMap();

        return gamesByBoard;
    }
}
