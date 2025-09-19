package kr.plusb3b.games.gamehub.domain.user.repository;


import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.vo.business.MbNickName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 상태별 조회
    Page<User> findByMbAct(int mbAct, Pageable pageable);

    // 역할별 조회
    Page<User> findByMbRole(User.Role mbRole, Pageable pageable);

    // 닉네임 검색
    Page<User> findByMbNickName_MbNickNameContaining(String keyword, Pageable pageable);

    // 신고 횟수가 특정 값 이상인 사용자
    Page<User> findByMbReportCntGreaterThanEqual(int reportCount, Pageable pageable);

    Optional<User> findUsersByMbNickName(MbNickName mbNickName);
}
