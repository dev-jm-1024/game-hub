package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.AdminPostFilesVmService;
import kr.plusb3b.games.gamehub.view.admin.AdminPostFilesVM;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminPostFilesVmServiceImpl implements AdminPostFilesVmService {

    private final BoardService boardService;
    private final PostsService postsService;
    private final PostFilesService postFilesService;

    public AdminPostFilesVmServiceImpl(BoardService boardService,
                                       PostsService postsService, PostFilesService postFilesService) {
        this.boardService = boardService;
        this.postsService = postsService;
        this.postFilesService = postFilesService;
    }

    @Override
    public Map<Board, List<PostFiles>> getPostFilesTop5() {
        // 모든 게시판을 키로 포함하되, 파일이 없는 경우 빈 리스트 반환
        return boardService.getAllBoards().stream()
                .collect(Collectors.toMap(
                        board -> board,
                        board -> postsService.getPostsByBoardId(board.getBoardId()).stream()
                                .flatMap(post -> post.getPostFiles().stream())
                                .limit(5) // Top 5로 제한
                                .collect(Collectors.toList())
                ));
    }

    @Override
    public List<AdminPostFilesVM> getPostFiles(String boardId) {

        List<AdminPostFilesVM> result = postsService.getPostsByBoardId(boardId).stream()
                .filter(Posts::isActivatePosts)
                .map(p -> new AdminPostFilesVM(
                        p.getBoard(),
                        p,
                        p.getPostFiles()
                )).collect(Collectors.toList());


        return result;
    }
}
