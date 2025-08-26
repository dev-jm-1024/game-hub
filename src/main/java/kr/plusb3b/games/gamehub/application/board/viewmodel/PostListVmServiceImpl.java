package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.dto.SummaryPostDto;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostListVmService;
import kr.plusb3b.games.gamehub.view.board.PostListVM;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PostListVmServiceImpl implements PostListVmService {

    private final PostsService postsService;
    private final BoardService boardService;

    public PostListVmServiceImpl(PostsService postsService, BoardService boardService) {
        this.postsService = postsService;
        this.boardService = boardService;
    }

    @Override
    public List<SummaryPostDto> getSummaryPostsByBoardId(String boardId) {

        List<SummaryPostDto> result = postsService.summaryPosts(boardId);

        if(result.isEmpty()) return Collections.emptyList();

        return result;
    }

    @Override
    public PostListVM getPostListVm(String boardId) {
        return new PostListVM(
                boardService.getBoardByBoardId(boardId),
                getSummaryPostsByBoardId(boardId)
        );
    }
}
