package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.data.user.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
}
