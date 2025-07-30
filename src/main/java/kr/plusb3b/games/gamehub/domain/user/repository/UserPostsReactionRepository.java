package kr.plusb3b.games.gamehub.domain.user.repository;


import java.util.Optional;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserPostsReactionRepository extends JpaRepository<UserPostsReaction, Long> {

    Optional<UserPostsReaction> findByUserAndPosts(User user, Posts posts);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE UserPostsReaction upr SET upr.reactionType = :reactionType " +
            "WHERE upr.posts.postId = :postId AND upr.user.mbId = :userId")
    int updateReactionByPostIdAndUserId(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            @Param("reactionType") UserPostsReaction.ReactionType reactionType
    );
}
