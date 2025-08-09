package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentsReactionCountRepository extends JpaRepository<CommentsReactionCount, Long> {

    // ✅ 좋아요 수 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE CommentsReactionCount cr SET cr.likeCount = cr.likeCount + 1 WHERE cr.comment.commentId = :commentId")
    int incrementLikeCountByCommentId(@Param("commentId") Long commentId);

    // ✅ 좋아요 수 감소
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE CommentsReactionCount cr SET cr.likeCount = cr.likeCount - 1 WHERE cr.comment.commentId = :commentId AND cr.likeCount > 0")
    int decrementLikeCountByCommentId(@Param("commentId") Long commentId);

    // ✅ 싫어요 수 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE CommentsReactionCount cr SET cr.dislikeCount = cr.dislikeCount + 1 WHERE cr.comment.commentId = :commentId")
    int incrementDislikeCountByCommentId(@Param("commentId") Long commentId);

    // ✅ 싫어요 수 감소
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE CommentsReactionCount cr SET cr.dislikeCount = cr.dislikeCount - 1 WHERE cr.comment.commentId = :commentId AND cr.dislikeCount > 0")
    int decrementDislikeCountByCommentId(@Param("commentId") Long commentId);

    // ✅ 신고 수 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE CommentsReactionCount cr SET cr.reportCount = cr.reportCount + 1 WHERE cr.comment.commentId = :commentId")
    int incrementReportCountByCommentId(@Param("commentId") Long commentId);

}
