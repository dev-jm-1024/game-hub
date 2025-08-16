package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long> {


    //boardId를 입력받아, 해당 게시판의 게시물이 존재하는 지 확인
    List<Posts> findByBoard_BoardId(String boardId);

    //posts 내용 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE Posts SET postTitle = :postTitle, postContent = :postContent, updatedAt = :updatedAt WHERE postId = :postId AND board.boardId = :boardId")
    int updatePostsByPostIdAndBoardId(
            @Param("postTitle") String postTitle,
            @Param("postContent") String postContent,
            @Param("updatedAt") LocalDate updatedAt,
            @Param("postId") Long postId,
            @Param("boardId") String boardId
    );

    //postAct = 0 으로 바꿔줌
    @Modifying
    @Transactional
    @Query("UPDATE Posts SET postAct = 0 WHERE postId = :postId")
    int deletePostsByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Posts p SET p.importantAct = 1 WHERE p.postId = :postId")
    int updateImportantActByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Posts p SET p.importantAct = 0 WHERE p.postId = :postId")
    int unsetImportantActByPostId(@Param("postId") Long postId);

}
