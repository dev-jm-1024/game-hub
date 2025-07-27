package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostsReactionCountRepository extends JpaRepository<PostsReactionCount, Long> {

    // ✅ 좋아요 수 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PostsReactionCount pr SET pr.likeCount = pr.likeCount + 1 WHERE pr.posts.postId = :postId")
    void incrementLikeCountByPostId(@Param("postId") Long postId);

    // ✅ 좋아요 수 감소
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PostsReactionCount pr SET pr.likeCount = pr.likeCount - 1 WHERE pr.posts.postId = :postId AND pr.likeCount > 0")
    void decrementLikeCountByPostId(@Param("postId") Long postId);

    // ✅ 싫어요 수 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PostsReactionCount pr SET pr.dislikeCount = pr.dislikeCount + 1 WHERE pr.posts.postId = :postId")
    void incrementDislikeCountByPostId(@Param("postId") Long postId);

    // ✅ 싫어요 수 감소
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PostsReactionCount pr SET pr.dislikeCount = pr.dislikeCount - 1 WHERE pr.posts.postId = :postId AND pr.dislikeCount > 0")
    void decrementDislikeCountByPostId(@Param("postId") Long postId);
}
