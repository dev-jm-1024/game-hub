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

    List<Posts> findByBoard_BoardId(String boardId, Pageable pageable);

    //List로 Posts 데이터 받기
    //해당 게시판의 아이디와 게시물의 아이디를 입력받아 해당 게시물 데이터 가져오기
    //예외처리를 보다 쉽게 처리하기 위해 Optional 로 처리
    Optional<Posts> findByBoard_BoardIdAndPostId(String boardId, Long postId);

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


    @Query("SELECT p FROM Posts p WHERE p.user.userAuth.authUserId = :authUserId AND p.board.boardId = :boardId")
    Optional<Posts> findPostsByUserAuth(@Param("authUserId") String authUserId,
                                        @Param("boardId") String boardId);


    Optional<Posts> findByPostIdAndBoard_BoardIdAndPostAct(Long postId, String boardId, int postAct);



}
