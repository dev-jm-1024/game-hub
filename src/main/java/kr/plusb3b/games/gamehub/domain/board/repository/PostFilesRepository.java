package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostFilesRepository extends JpaRepository<PostFiles, Long> {

    Optional<PostFiles> findPostFilesByPost_PostId(Long postPostId);

    // 메소드명 수정
    List<PostFiles> findByPostPostId(Long postId);

    @Query("SELECT pf FROM PostFiles pf WHERE pf.post.postId = :postId")
    List<PostFiles> findPostFilesByPostId(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostFiles pf WHERE pf.post.postId = :postId")
    int deletePostFilesByPostId(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostFiles pf WHERE pf.post.postId = :postId AND pf.fileUrl NOT IN :keepUrls")
    int deleteRemovedPostFiles(@Param("postId") Long postId, @Param("keepUrls") List<String> keepUrls);
}