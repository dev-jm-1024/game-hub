package kr.plusb3b.games.gamehub.api.service.Board;

import kr.plusb3b.games.gamehub.api.dto.board.Posts;

import java.util.List;
import java.util.Map;

public interface BoardService {

    //게시판과 각 게시판의 게시물 가져오기 -- 최신순 5개 : 내림차순 정렬
    Map<String, List<Posts>> loadTop5LatestPostsByBoard();

    //게시판의 이름 변경
    void renameBoard(String oldName, String newName);

    //게시판의 상태 변경하기
    void deleteBoard();

    boolean validateBoard(String boardId);
}
