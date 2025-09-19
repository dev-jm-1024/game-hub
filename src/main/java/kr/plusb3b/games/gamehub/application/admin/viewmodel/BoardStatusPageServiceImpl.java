package kr.plusb3b.games.gamehub.application.admin.viewmodel;

import kr.plusb3b.games.gamehub.application.admin.AdminBoardConfig;
import kr.plusb3b.games.gamehub.domain.admin.service.viewmodel.BoardStatusPageService;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.service.admin.AdminBoardService;
import kr.plusb3b.games.gamehub.view.admin.BoardStatusPage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BoardStatusPageServiceImpl implements BoardStatusPageService {

    private final AdminBoardService adminBoardService;
    private final AdminBoardConfig adminBoardConfig;

    private final String NOTICE = "notice";

    public BoardStatusPageServiceImpl(AdminBoardService adminBoardService, AdminBoardConfig adminBoardConfig) {
        this.adminBoardService = adminBoardService;
        this.adminBoardConfig = adminBoardConfig;
    }

    @Override
    public BoardStatusPage getBoardStatusPage() {
        List<Board> boardList = adminBoardService.getAllBoards().stream()
                .filter(b -> !b.getBoardId().equals(NOTICE))
                .toList();

        Map<String, String> boardApiPaths = adminBoardConfig.getAdminBoardPaths();

        return new BoardStatusPage(boardList, boardApiPaths);
    }

}
