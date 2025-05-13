package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.api.dto.user.UserPrivate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPrivateRepository extends JpaRepository<UserPrivate, Long> {
}
