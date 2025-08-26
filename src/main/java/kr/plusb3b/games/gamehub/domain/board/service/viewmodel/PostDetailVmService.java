package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.view.board.PostDetailVM;

public interface PostDetailVmService {

    PostDetailVM getPostDetailVm(String boardId, Long postId, HttpServletRequest request);

}
