package kr.plusb3b.games.gamehub.api.service.Board;

import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.board.Posts;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<String, List<Posts>> loadTop5LatestPostsByBoard() {

        Map<String, List<Posts>> result = new HashMap<>();

        //활성화된 게시판 가져오기
        List<Board> boardList = boardRepo.findAll().stream()
                .filter(Board::activateBoard)
                .toList();

        List<Posts> postsList;

        for(Board board : boardList) {

            //게시판의 아이디를 통해 게시물 가져오기
            postsList = postsRepo.findByBoard_BoardId(board.getBoardId()).stream()
                    .filter(Posts::isActivatePosts)
                    .sorted(Comparator.comparing(Posts::getCreatedAt).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            result.put(board.getBoardId(), postsList);
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
