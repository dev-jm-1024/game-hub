package kr.plusb3b.games.gamehub.application.admin.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.dto.CreateBoardDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.service.admin.AdminBoardService;
import kr.plusb3b.games.gamehub.domain.board.vo.business.BoardName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminBoardServiceImpl implements AdminBoardService {

    private final BoardRepository boardRepo;

    public AdminBoardServiceImpl(BoardRepository boardRepo) {
        this.boardRepo = boardRepo;
    }

    @Override
    public List<Board> getAllBoards() {
        return boardRepo.findAll();
    }

    @Override
    @Transactional
    public boolean renameBoard(String boardId, String newName) {
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. ID: " + boardId));

        // VO의 도메인 규칙을 적용
        board.changeBoardName(BoardName.of(newName));

        // save() 호출 불필요 (Dirty Checking이 알아서 flush)
        return true;
    }



    @Override
    @Transactional // Service 레벨에서 트랜잭션 관리
    public boolean changeBoardStatus(String boardId, int status) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. ID: " + boardId));

        board.changeBoardStatus(status);

        return true;
    }

    @Override
    //게시판 작성
    public int createBoard(CreateBoardDto createBoardDto){

        String boardId = UUID.randomUUID().toString().substring(0, 10);

        Board result = boardRepo.save(new Board(
                boardId,
                BoardName.of(createBoardDto.getBoardName()),
                1
            )
        );

        if(result == null)
            return 0;

        return 1;
    }

    //게시판 이름 중복 확인
    @Override
    public boolean isDuplicateBoardName(String boardName){


        return boardRepo.existsBoardByBoardName(BoardName.of(boardName));
    }


}
