package kr.plusb3b.games.gamehub.repository.boardrepo;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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



}
