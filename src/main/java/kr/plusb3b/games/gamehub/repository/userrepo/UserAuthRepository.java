package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    // Spring JPA 메서드 규칙에 따라 필드명을 그대로 메서드명에 사용 (쿼리 자동 생성됨)
    Optional<UserAuth> findByAuthUserId(String authUserId);

    // 사용자 아이디 + 패스워드로 조회 (로그인용)
    @Query("SELECT ua FROM UserAuth ua WHERE ua.authUserId = :userId AND ua.authPassword = :authPassword")
    Optional<UserAuth> findByUserAuth(@Param("userId") String authUserId, @Param("authPassword") String authPassword);
}
