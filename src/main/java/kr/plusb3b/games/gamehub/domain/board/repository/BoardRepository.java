package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {

//    //아이디를 통해 게시물 이름 반환
//    @Query("SELECT board_name FROM Board WHERE board_id = :boardId")
//    public List<String> findBoardNameById(@Param("boardId") Long boardId);



}
