package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    //로그인한 아이디를 바탕으로 사용자의 고유 아이디를 가져와야함. 그게 mb_id임
    @Query("SELECT auth_user_id FROM UserAuth WHERE UserAuth.auth_user_id = :loginId")
    User findUserByLoginId(@Param("loginId") String loginId);
}
