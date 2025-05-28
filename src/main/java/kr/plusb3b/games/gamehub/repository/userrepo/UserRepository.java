package kr.plusb3b.games.gamehub.repository.userrepo;


import kr.plusb3b.games.gamehub.api.dto.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT ua.user FROM UserAuth ua WHERE ua.authUserId = :authUserId")
    User findByAuthUserId(@Param("authUserId") String authUserId);

    // 로그인 ID(authUserId)는 UserAuth에 있지만, 연결된 User를 통해 가져올 수 있도록 메서드 작성
    Optional<User> findByMbNickname(String mbNickname);

    // 또는 username이 로그인 ID(authUserId)라면 아래와 같이 UserAuth 연동용 쿼리도 가능
    Optional<User> findByUserAuth_AuthUserId(String authUserId);

    @Query("SELECT u.mbAct FROM User u WHERE u.userAuth.authUserId = :authUserId")
    int findMbActByAuthUserId(@Param("authUserId") String authUserId);

    //회원계정 비활성화 -- 탈퇴
    @Modifying
    @Query("UPDATE User SET mbAct = 0 WHERE userAuth.authUserId = :authUserId AND mbAct = 1")
    int updateMbActByAuthUserId(@Param("authUserId") String authUserId);

}
