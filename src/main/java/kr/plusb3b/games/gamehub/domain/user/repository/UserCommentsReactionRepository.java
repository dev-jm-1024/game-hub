package kr.plusb3b.games.gamehub.domain.user.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserCommentsReactionRepository extends JpaRepository<UserCommentsReaction, Long> {

    // ✅ 유저가 댓글에 남긴 반응 조회
    Optional<UserCommentsReaction> findByUserAndComments(User user, Comments comments);

    // ✅ 유저의 댓글 반응 수정 (LIKE, DISLIKE, REPORT 등)
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE UserCommentsReaction ucr SET ucr.reactionType = :reactionType " +
            "WHERE ucr.comments.commentId = :commentId AND ucr.user.mbId = :userId")
    int updateReactionTypeByCommentIdAndUserId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId,
            @Param("reactionType") UserCommentsReaction.ReactionType reactionType
    );
}
