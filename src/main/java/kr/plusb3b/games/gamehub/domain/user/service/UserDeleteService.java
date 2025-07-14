package kr.plusb3b.games.gamehub.domain.user.service;

public interface UserDeleteService {

    boolean deactivateUser(long mbId, String password);
}
