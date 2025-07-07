package kr.plusb3b.games.gamehub.domain.user.repository;

import jakarta.transaction.Transactional;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {

    // Spring JPA 메서드 규칙에 따라 필드명을 그대로 메서드명에 사용 (쿼리 자동 생성됨)
    Optional<UserAuth> findByAuthUserId(String authUserId);

    //authLastLogin 입력 받아 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE UserAuth SET authLastLogin = :authLastLogin WHERE authUserId = :authUserId")
    int updateLastLoginByUserId(@Param("authLastLogin") LocalDateTime authLastLogin,
                               @Param("authUserId") String authUserId);

    @Query("SELECT ua FROM UserAuth ua WHERE ua.user.mbId = :mbId")
    Optional<UserAuth> findByUser_MbId(@Param("mbId") Long mbId);



    @Transactional
    @Modifying
    @Query("UPDATE UserAuth ua SET ua.authUserId = :authUserId WHERE ua.user.mbId = :mbId")
    int updateUserAuth(@Param("authUserId") String authUserId, @Param("mbId") Long mbId);

}
