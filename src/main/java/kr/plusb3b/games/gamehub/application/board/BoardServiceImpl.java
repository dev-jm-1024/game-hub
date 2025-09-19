package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import kr.plusb3b.games.gamehub.domain.game.repository.GamesRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    public List<String> getBoardIds() {
        return boardRepo.findAll().stream()
                .filter(Board::activateBoard)
                .map(Board::getBoardId)
                .toList();
    }

    @Override
    public List<Board> getAllBoards(){

        return boardRepo.findAll().stream()
                .filter(Board::activateBoard)
                .toList();

    }

}
