package kr.plusb3b.games.gamehub.domain.user.repository;

import kr.plusb3b.games.gamehub.domain.user.entity.UserLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfo, Long> {
}
