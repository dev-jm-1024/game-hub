package kr.plusb3b.games.gamehub.domain.board.service;

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

    //게시판 데이터 가져오기 - 관리자 용도
    List<Board> getAllBoards();

    //게시판의 이름 변경 - 관리자 용도
    boolean renameBoard(String boardId, String newName);

    //게시판 제거하기 - 관리자 용도
    boolean changeBoardStatus(String boardId, int status);

    // 편의 메서드
    boolean deactivateBoard(String boardId);
    boolean activateBoard(String boardId);
}
