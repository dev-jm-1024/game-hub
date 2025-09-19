package kr.plusb3b.games.gamehub.domain.admin.service.viewmodel;

import kr.plusb3b.games.gamehub.view.admin.AdminPostsDetailVM;

public interface AdminPostsDetailVmService {

    AdminPostsDetailVM getAdminPostsDetailVm(String boardId, Long postId);
}
