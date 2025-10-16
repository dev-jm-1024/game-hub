package kr.plusb3b.games.gamehub.domain.user.repository;

import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthUserId;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, AuthUserId> {

    // AuthUserId VO를 사용한 조회
    Optional<UserAuth> findByAuthUserId(AuthUserId authUserId);

    // User 객체로 조회
    Optional<UserAuth> findByUser(User user);

    @Query("SELECT ua.authUserId FROM UserAuth ua WHERE ua.user.userPrivate.priEmail = :priEmail")
    Optional<String> findUserAuthByPriEmail(@Param("priEmail") PriEmail priEmail);
}