package kr.plusb3b.games.gamehub.repository.userrepo;


import kr.plusb3b.games.gamehub.api.dto.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
