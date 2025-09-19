package kr.plusb3b.games.gamehub.domain.user.repository;

import jakarta.transaction.Transactional;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.vo.business.AuthUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, AuthUserId> {

    // AuthUserId VO를 사용한 조회
    Optional<UserAuth> findByAuthUserId(AuthUserId authUserId);

    // String으로 조회하는 편의 메소드
    @Query("SELECT ua FROM UserAuth ua WHERE ua.authUserId = :authUserId")
    Optional<UserAuth> findByAuthUserIdString(@Param("authUserId") String authUserId);

    // 마지막 로그인 시간 업데이트 (AuthUserId VO 사용)
    @Transactional
    @Modifying
    @Query("UPDATE UserAuth ua SET ua.authLastLogin = :authLastLogin WHERE ua.authUserId = :authUserId")
    int updateLastLoginByUserId(@Param("authLastLogin") LocalDateTime authLastLogin,
                                @Param("authUserId") AuthUserId authUserId);

    // 마지막 로그인 시간 업데이트 (String 사용)
    @Transactional
    @Modifying
    @Query("UPDATE UserAuth ua SET ua.authLastLogin = :authLastLogin WHERE ua.authUserId.authUserId = :authUserId")
    int updateLastLoginByUserIdString(@Param("authLastLogin") LocalDateTime authLastLogin,
                                      @Param("authUserId") String authUserId);

    // User의 mbId로 조회
    @Query("SELECT ua FROM UserAuth ua WHERE ua.user.mbId = :mbId")
    Optional<UserAuth> findByUser_MbId(@Param("mbId") Long mbId);

    // AuthUserId 업데이트 (AuthUserId VO 사용)
    @Transactional
    @Modifying
    @Query("UPDATE UserAuth ua SET ua.authUserId = :authUserId WHERE ua.user.mbId = :mbId")
    int updateUserAuth(@Param("authUserId") AuthUserId authUserId, @Param("mbId") Long mbId);

    // AuthUserId 업데이트 (String 사용)
    @Transactional
    @Modifying
    @Query("UPDATE UserAuth ua SET ua.authUserId.authUserId = :authUserId WHERE ua.user.mbId = :mbId")
    int updateUserAuthString(@Param("authUserId") String authUserId, @Param("mbId") Long mbId);

    // User 객체로 조회
    Optional<UserAuth> findByUser(User user);
}