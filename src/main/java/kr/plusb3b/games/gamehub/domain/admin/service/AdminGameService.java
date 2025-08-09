package kr.plusb3b.games.gamehub.domain.admin.service;

public interface AdminGameService {

    //게임 승인
    boolean approveGame(Long gameId);

    //게임 거절
    boolean rejectGame(Long gameId);
}
