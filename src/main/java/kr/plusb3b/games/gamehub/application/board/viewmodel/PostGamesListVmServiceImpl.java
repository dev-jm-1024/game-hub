package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostGamesListVmService;
import kr.plusb3b.games.gamehub.domain.game.dto.SummaryGamesDto;
import kr.plusb3b.games.gamehub.domain.game.service.GameMetadataService;
import kr.plusb3b.games.gamehub.view.board.PostGamesListVM;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PostGamesListVmServiceImpl implements PostGamesListVmService {

    private final BoardService boardService;
    private final GameMetadataService gameMetadataService;

    public PostGamesListVmServiceImpl(BoardService boardService, GameMetadataService gameMetadataService) {
        this.boardService = boardService;
        this.gameMetadataService = gameMetadataService;
    }

    @Override
    public List<SummaryGamesDto> getSummaryGamesByBoardId(String boardId) {

        List<SummaryGamesDto> result = gameMetadataService.getSummaryGame(boardId);

        if(result.isEmpty()) return Collections.emptyList();

        return result;
    }

    @Override
    public PostGamesListVM getPostGameListVm(String boardId) {
        return new PostGamesListVM(
                boardService.getBoardByBoardId(boardId),
                getSummaryGamesByBoardId(boardId)
        );
    }
}
