package kr.plusb3b.games.gamehub.repository.userrepo;


import kr.plusb3b.games.gamehub.api.dto.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //뭔가 안되어지고 있음
    @Query("SELECT u FROM User u WHERE u.userAuth.authUserId = :authUserId")
    Optional<User> findByUserAuth_AuthUserId(@Param("authUserId") String authUserId);

    //회원계정 비활성화 -- 탈퇴
    @Transactional
    @Modifying
    @Query("UPDATE User SET mbAct = 0 WHERE userAuth.authUserId = :authUserId AND mbAct = 1")
    int updateMbActByAuthUserId(@Param("authUserId") String authUserId);

    //프로필 업데이트 메소드
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.mbNickname = :mbNickName, u.mbProfileUrl = :mbProfileUrl, u.mbStatusMessage = :mbStatusMessage WHERE u.mbId = :mbId")
    int updateUserByMbId(
            @Param("mbNickName") String mbNickName,
            @Param("mbProfileUrl") String mbProfileUrl,
            @Param("mbStatusMessage") String mbStatusMessage,
            @Param("mbId") Long mbId
    );


}
