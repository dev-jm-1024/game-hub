package kr.plusb3b.games.gamehub.domain.user.repository;


import java.util.Optional;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostsReactionRepository extends JpaRepository<UserPostsReaction, Long> {

    Optional<UserPostsReaction> findByUserAndPost(User user, Posts post);
}
