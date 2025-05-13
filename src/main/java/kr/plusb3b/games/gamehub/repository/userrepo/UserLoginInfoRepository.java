package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.api.dto.user.UserLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfo, Long> {
}
