package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.BoardService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.AdminPostsDetailVmService;
import kr.plusb3b.games.gamehub.view.admin.AdminPostsDetailVM;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPostsDetailVmServiceImpl implements AdminPostsDetailVmService {

    private final BoardService boardService;
    private final PostsService postsService;
    private final PostFilesService postFilesService;

    public AdminPostsDetailVmServiceImpl(BoardService boardService, PostsService postsService, PostFilesService postFilesService) {
        this.boardService = boardService;
        this.postsService = postsService;
        this.postFilesService = postFilesService;
    }

    @Override
    public AdminPostsDetailVM getAdminPostsDetailVm(String boardId, Long postId) {
        Posts post = postsService.detailPosts(boardId, postId);
        
        if (post == null || !post.isActivatePosts()) {
            return null;
        }
        
        return new AdminPostsDetailVM(
                post.getBoard(),
                post,
                post.getPostFiles(),
                post.getReactionCount()
        );
    }
}