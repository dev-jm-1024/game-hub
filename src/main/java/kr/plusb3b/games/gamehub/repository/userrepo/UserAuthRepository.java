package kr.plusb3b.games.gamehub.repository.userrepo;

import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.api.dto.user.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    //로그인한 아이디를 바탕으로 사용자의 고유 아이디를 가져와야함. 그게 mb_id임
    @Query("SELECT auth_user_id FROM UserAuth WHERE UserAuth.auth_user_id = :loginId")
    User findUserByLoginId(@Param("loginId") String loginId);

    //사용자의 해시화된 패스워드와 로그인한 아이디를 통해 객체가 존재하는 지 판단
    @Query("SELECT UserAuth FROM UserAuth WHERE UserAuth.auth_user_id = :userId AND UserAuth.auth_password = :uwerPw")
    Optional<UserAuth> findByUserAuth(@Param("userId") String userId, @Param("userPw") String userPw);

    // 사용자 아이디로만 찾기 (비밀번호는 따로 matches() 처리)
    Optional<UserAuth> findByAuth_user_id(String auth_user_id);
}
