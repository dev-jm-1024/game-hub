package kr.plusb3b.games.gamehub.application.user.viewmodel;

import kr.plusb3b.games.gamehub.domain.user.service.viewmodel.UserLoginRecordVmService;
import kr.plusb3b.games.gamehub.view.user.UserLoginRecordVM;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLoginRecordVmServiceImpl implements UserLoginRecordVmService {


    @Override
    public List<UserLoginRecordVM> getUserLoginRecord(Long mbId) {
        return List.of();
    }
}
