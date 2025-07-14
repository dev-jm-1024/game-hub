package kr.plusb3b.games.gamehub.domain.board.repository;


import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {


    // interface CommentsRepository
    List<Comments> findByPosts_PostIdAndCommentActOrderByCreatedAtDesc(Long postId, int commentAct);
}
