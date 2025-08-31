package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.view.admin.AdminPostFilesVM;
import java.util.*;

public interface AdminPostFilesVmService {

    Map<Board, List<PostFiles>> getPostFilesTop5();

    List<AdminPostFilesVM> getPostFiles(String boardId);

}
