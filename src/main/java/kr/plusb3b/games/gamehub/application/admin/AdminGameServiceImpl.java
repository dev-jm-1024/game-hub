package kr.plusb3b.games.gamehub.application.admin;

import kr.plusb3b.games.gamehub.domain.admin.service.AdminGameService;
import org.springframework.stereotype.Service;

@Service
public class AdminGameServiceImpl implements AdminGameService {

    @Override
    public boolean approveGame(Long gameId) {
        return true;
    }

    @Override
    public boolean rejectGame(Long gameId) {
        return false;
    }
}
