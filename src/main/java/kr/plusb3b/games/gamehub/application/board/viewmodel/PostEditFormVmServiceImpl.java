package kr.plusb3b.games.gamehub.application.board.viewmodel;

import kr.plusb3b.games.gamehub.domain.board.entity.PostFiles;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostEditFormVmService;
import kr.plusb3b.games.gamehub.view.board.PostEditFormVM;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostEditFormVmServiceImpl implements PostEditFormVmService {

    private final PostsService postsService;
    private final PostFilesService postFilesService;

    public PostEditFormVmServiceImpl(PostsService postsService, PostFilesService postFilesService) {
        this.postsService = postsService;
        this.postFilesService = postFilesService;
    }

    @Override
    public PostEditFormVM getPostEditFormVm(String boardId, Long postId, HttpServletRequest request) {

        // 1. 게시물 조회
        Posts posts = postsService.detailPosts(boardId, postId);

        // 2. 권한 확인
        boolean isAuthor = postsService.isAuthor(request, postId);
        if (!isAuthor) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // 3. 첨부파일 조회
        List<PostFiles> postFilesList = postFilesService.getPostFiles(postId);
        boolean hasPostFile = !postFilesList.isEmpty();

        return new PostEditFormVM(
                posts,
                postFilesList,
                boardId,
                isAuthor,
                hasPostFile
        );
    }
}
