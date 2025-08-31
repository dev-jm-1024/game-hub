package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.AdminSummaryPostVmService;
import kr.plusb3b.games.gamehub.view.admin.AdminSummaryPostVM;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminSummaryPostVmServiceImpl implements AdminSummaryPostVmService {

    private final BoardService boardService;
    private final PostsService postsService;

    public AdminSummaryPostVmServiceImpl(BoardService boardService, PostsService postsService) {
        this.boardService = boardService;
        this.postsService = postsService;
    }

    @Override
    public List<AdminSummaryPostVM> getAdminSummaryPostVm(String boardId) {

        List<AdminSummaryPostVM> result = postsService.getAllPosts().stream()
                .filter(Posts::isActivatePosts)
                .map(p -> new AdminSummaryPostVM(
                        p.getBoard(),
                        p.getPostId(),
                        p.getPostTitle(),
                        p.getReactionCount()
                )).collect(Collectors.toList());


        return result;
    }
}
