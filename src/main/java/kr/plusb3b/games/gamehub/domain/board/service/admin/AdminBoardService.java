package kr.plusb3b.games.gamehub.domain.board.service.admin;

import kr.plusb3b.games.gamehub.domain.board.dto.CreateBoardDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;

import java.util.List;

public interface AdminBoardService {

    //게시판 데이터 가져오기 - 관리자 용도
    List<Board> getAllBoards();

    //게시판의 이름 변경 - 관리자 용도
    boolean renameBoard(String boardId, String newName);

    //게시판 제거하기 - 관리자 용도
    boolean changeBoardStatus(String boardId, int status);

    //게시판 생성하기 - 관리자 용도
    int createBoard(CreateBoardDto createBoardDto);

    //게시판 이름 중복 확인 - 관리자 용도
    boolean isDuplicateBoardName(String boardName);
}
