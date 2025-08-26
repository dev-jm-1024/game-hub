package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.dto.CreateBoardDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {


    private final BoardRepository boardRepo;
    private final PostsRepository postsRepo;
    private final GamesRepository gameRepo;

    public BoardServiceImpl(BoardRepository boardRepo, PostsRepository postsRepo,
                            GamesRepository gameRepo) {
        this.boardRepo = boardRepo;
        this.postsRepo = postsRepo;
        this.gameRepo = gameRepo;
    }

    @Override
    public Map<Board, List<Posts>> loadTop5LatestPostsByBoard() {

        Map<Board, List<Posts>> result = new LinkedHashMap<>(); // 순서 유지하고 싶으면 LinkedHashMap 추천

        //게임 게시판은 제외 - 별도 제공
        List<Board> boardList = boardRepo.findAll().stream()
                .filter(Board::activateBoard)
                .filter(b -> !b.getBoardId().equals("gameBoard"))
                .toList();

        for (Board board : boardList) {
            List<Posts> postsList = postsRepo.findByBoard_BoardId(board.getBoardId()).stream()
                    .filter(Posts::isActivatePosts)
                    .sorted(Comparator.comparing(Posts::getCreatedAt).reversed())
                    .limit(5)
                    .toList();

            result.put(board, postsList);
        }

        return result;
    }

    @Override
    //게임게시판 제공
    public Map<Board, List<Games>> loadTop5LatestGamesByBoard() {

        Map<Board, List<Games>> result = new LinkedHashMap<>();

        //boardId의 값이 gameId인 것
        String gBoardId = "gameBoard";
        Board gBoard = boardRepo.findById(gBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        List<Games> gamesList = gameRepo.findGamesByBoard(gBoard).stream()
                .filter(g -> g.isVisible() && g.isStatusActive())
                .sorted(Comparator.comparing(Games::getCreatedAt).reversed())
                .limit(5)
                .toList();

        result.put(gBoard, gamesList);

        return result;


    }

    @Override
    public boolean validateBoard(String boardId){
        if (boardId == null || boardId.isBlank()) return false;
        return boardRepo.existsById(boardId);
    }

    @Override
    public Board getBoardByBoardId(String boardId) {
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        return board;
    }

    /***************************************************Admin Method***************************************************/

    @Override
    public List<Board> getAllBoards() {
        return boardRepo.findAll();
    }

    @Override
    @Transactional // Service 레벨에서 트랜잭션 관리
    public boolean renameBoard(String boardId, String newName) {
        // 입력값 검증
        if (boardId == null || boardId.trim().isEmpty()) {
            throw new IllegalArgumentException("게시판 ID는 필수입니다.");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("새로운 게시판 이름은 필수입니다.");
        }

        int updatedRows = boardRepo.updateBoardNameByBoardId(newName.trim(), boardId);

        // 업데이트 결과 확인
        if (updatedRows == 0) {
            throw new RuntimeException("게시판을 찾을 수 없습니다. ID: " + boardId);
        }

        return updatedRows > 0;
    }


    @Override
    @Transactional // Service 레벨에서 트랜잭션 관리
    public boolean changeBoardStatus(String boardId, int status) {
        // 입력값 검증
        if (boardId == null || boardId.trim().isEmpty()) {
            throw new IllegalArgumentException("게시판 ID는 필수입니다.");
        }
        // status 값 검증 (0: 비활성, 1: 활성으로 가정)
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("상태값은 0(비활성) 또는 1(활성)이어야 합니다.");
        }

        int updatedRows = boardRepo.updateBoardActByBoardId(status, boardId);

        // 업데이트 결과 확인
        if (updatedRows == 0) {
            throw new RuntimeException("게시판을 찾을 수 없습니다. ID: " + boardId);
        }

        return updatedRows > 0;
    }

    @Override
    //게시판 작성
    public int createBoard(CreateBoardDto createBoardDto){

        String boardId = UUID.randomUUID().toString().substring(0, 10);

        Board board = new Board(
                boardId,
                createBoardDto.getBoardName(),
                1
        );

        Board result = boardRepo.save(board);
        if(result == null)
            return 0;

        return 1;
    }

    //게시판 이름 중복 확인
    @Override
    public boolean isDuplicateBoardName(String boardName){

        List<Board> result = boardRepo.findBoardsByBoardName(boardName);
        return result.isEmpty();
    }

    // 편의 메서드 추가
    @Transactional
    public boolean deactivateBoard(String boardId) {
        return changeBoardStatus(boardId, 0); // 0: 비활성
    }

    @Transactional
    public boolean activateBoard(String boardId) {
        return changeBoardStatus(boardId, 1); // 1: 활성
    }
}
