package kr.plusb3b.games.gamehub.domain.user.service.viewmodel;

import kr.plusb3b.games.gamehub.view.user.UserLoginRecordVM;

import java.util.List;

public interface UserLoginRecordVmService {

    List<UserLoginRecordVM> getUserLoginRecord(Long mbId);
}
