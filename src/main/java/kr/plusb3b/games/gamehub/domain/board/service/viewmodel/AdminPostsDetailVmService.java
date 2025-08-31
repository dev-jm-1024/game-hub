package kr.plusb3b.games.gamehub.domain.board.service.viewmodel;

import kr.plusb3b.games.gamehub.view.admin.AdminPostsDetailVM;

import java.util.List;

public interface AdminPostsDetailVmService {

    AdminPostsDetailVM getAdminPostsDetailVm(String boardId, Long postId);
}
