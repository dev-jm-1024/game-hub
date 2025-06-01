package kr.plusb3b.games.gamehub.repository.userrepo;

import jakarta.transaction.Transactional;
import kr.plusb3b.games.gamehub.api.dto.user.UserPrivate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserPrivateRepository extends JpaRepository<UserPrivate, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserPrivate up SET up.priBirth = :priBirth, up.priEmail = :priEmail, up.priGender = :priGender WHERE up.user.mbId = :mbId")
    int updateUserPrivate(
            @Param("priBirth") LocalDate priBirth,
            @Param("priEmail") String priEmail,
            @Param("priGender") String priGender,
            @Param("mbId") Long mbId
    );

    @Query("SELECT UserPrivate FROM UserPrivate WHERE UserPrivate.user.mbId = :mbId")
    Optional<UserPrivate> findUserPrivateByMbId(@Param("mbId") Long mbId);

}
