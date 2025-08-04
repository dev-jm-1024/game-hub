package kr.plusb3b.games.gamehub.domain.user.repository;


import kr.plusb3b.games.gamehub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 신고횟수 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.mbReportCnt = u.mbReportCnt + 1 WHERE u.mbId = :mbId")
    int incrementReportCountByMbId(@Param("mbId") Long mbId);

}
