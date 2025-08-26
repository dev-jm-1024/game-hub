package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;
import kr.plusb3b.games.gamehub.view.board.PostListVM;

import java.util.List;

public interface PostListVmService{

    List<SummaryPostDto> getSummaryPostsByBoardId(String boardId);

    PostListVM getPostListVm(String boardId);


}
