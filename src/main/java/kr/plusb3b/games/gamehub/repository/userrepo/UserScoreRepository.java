package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.data.user.UserScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
}
