package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {


    private final BoardRepository boardRepo;
    private final PostsRepository postsRepo;

    public BoardServiceImpl(BoardRepository boardRepo, PostsRepository postsRepo) {
        this.boardRepo = boardRepo;
        this.postsRepo = postsRepo;
    }

    @Override
    public Map<Board, List<Posts>> loadTop5LatestPostsByBoard() {

        Map<Board, List<Posts>> result = new LinkedHashMap<>(); // 순서 유지하고 싶으면 LinkedHashMap 추천

        List<Board> boardList = boardRepo.findAll().stream()
                .filter(Board::activateBoard)
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
    public void renameBoard(String oldName, String newName) {

    }

    @Override
    public void deleteBoard() {

    }

    @Override
    public boolean validateBoard(String boardId){
        if (boardId == null || boardId.isBlank()) return false;
        return boardRepo.existsById(boardId);
    }
}
