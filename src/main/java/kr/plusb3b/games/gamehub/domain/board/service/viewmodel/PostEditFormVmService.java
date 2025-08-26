package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import kr.plusb3b.games.gamehub.view.board.PostEditFormVM;

import jakarta.servlet.http.HttpServletRequest;

public interface PostEditFormVmService {

    PostEditFormVM getPostEditFormVm(String boardId, Long postId, HttpServletRequest request);
}
