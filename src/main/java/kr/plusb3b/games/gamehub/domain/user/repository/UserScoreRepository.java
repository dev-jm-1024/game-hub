package kr.plusb3b.games.gamehub.domain.user.repository;

import kr.plusb3b.games.gamehub.domain.user.entity.UserScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
}
