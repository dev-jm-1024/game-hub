package kr.plusb3b.games.gamehub.domain.board.repository;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.vo.business.BoardName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {


    @Modifying
    @Query("UPDATE Board b SET b.boardName = :boardName WHERE b.boardId = :boardId")
    int updateBoardNameByBoardId(@Param("boardName") String boardName, @Param("boardId") String boardId);

    @Modifying
    @Query("UPDATE Board b SET b.boardAct = :boardAct WHERE b.boardId = :boardId")
    int updateBoardActByBoardId(@Param("boardAct") int boardAct, @Param("boardId") String boardId);

    List<Board> findBoardsByBoardName(String boardName);

    boolean existsBoardByBoardName(BoardName boardName);
}
