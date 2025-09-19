package kr.plusb3b.games.gamehub.domain.board.service;

import kr.plusb3b.games.gamehub.domain.board.dto.CreateBoardDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;

import java.util.List;
import java.util.Map;

public interface BoardService {

    //게시판과 각 게시판의 게시물 가져오기 -- 최신순 5개 : 내림차순 정렬
    Map<Board, List<Posts>> loadTop5LatestPostsByBoard();

    //gameBoard(게임 게시판)에 최신순 - 5개 정렬 : 내림차순 정렬
    Map<Board, List<Games>> loadTop5LatestGamesByBoard();

    boolean validateBoard(String boardId);

    Board getBoardByBoardId(String boardId);

    List<String> getBoardIds();

    List<Board> getAllBoards();

}
