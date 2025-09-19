package kr.plusb3b.games.gamehub.domain.admin.service.viewmodel;

import kr.plusb3b.games.gamehub.view.admin.AdminSummaryPostVM;

import java.util.List;

public interface AdminSummaryPostVmService {

    List<AdminSummaryPostVM> getAdminSummaryPostVm(String boardId);
}
