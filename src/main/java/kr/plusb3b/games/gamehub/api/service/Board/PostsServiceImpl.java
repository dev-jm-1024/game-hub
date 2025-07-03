package kr.plusb3b.games.gamehub.api.service.Board;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.api.dto.board.*;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.repository.boardrepo.BoardRepository;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostsServiceImpl implements PostsService {

    private final BoardRepository boardRepo;
    private final PostsRepository postsRepo;
    private final AccessControlService access;

    public PostsServiceImpl(BoardRepository boardRepo, PostsRepository postsRepo, AccessControlService access) {
        this.boardRepo = boardRepo;
        this.postsRepo = postsRepo;
        this.access = access;
    }

    @Override
    public void createPost(PostRequestDto postRequestDto, CreatePostsVO createPostsVO, String boardId, HttpServletRequest request) {

        Board board = boardRepo.findById(boardId).get();
        User user = access.getAuthenticatedUser(request);

        Posts posts = new Posts(
                board, user,
                postRequestDto.getPostTitle(),
                postRequestDto.getPostContent(),
                createPostsVO.getViewCount(),
                LocalDate.now(),
                createPostsVO.getUpdatedAt(),
                createPostsVO.getPostAct()

        );

        Posts result = postsRepo.save(posts);

    }

    @Override
    public void updatePost(PostRequestDto postRequestDto, String boardId, Long postId, HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);

        int updateResult = postsRepo.updatePostsByPostIdAndBoardId(
                postRequestDto.getPostTitle(),
                postRequestDto.getPostContent(),
                LocalDate.now(),
                postId,
                boardId
        );
    }

    @Override
    public List<SummaryPostDto> summaryPosts(String boardId) {

        List<SummaryPostDto> result = postsRepo.findByBoard_BoardId(boardId).stream()
                .filter(Posts::isActivatePosts)
                .map(post -> new SummaryPostDto(
                        post.getUser().getMbNickname(),
                        post.getPostTitle(),
                        post.getCreatedAt()
                        //post.getViewCount()
                ))
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public Posts detailPosts(String boardId, Long postId) {

        Optional<Posts> postOpt = postsRepo.findByBoard_BoardIdAndPostId(boardId,postId);

        Posts result;

        if(postOpt.isPresent()) {
            result = postOpt.get();

            return result;
        }

        return null;
    }

    @Override
    public boolean deactivatePost(Long postId, HttpServletRequest request) {

        User user = access.getAuthenticatedUser(request);
        int deactivatePosts = postsRepo.deletePostsByPostId(postId);

        if(deactivatePosts > 0) {
            return true;
        }

        return false;
    }

    @Override
    public void increaseViewCount(Long postId) {

    }

    @Override
    public boolean isAuthor(HttpServletRequest request, Posts posts) {

        User user = access.getAuthenticatedUser(request);
        User author = posts.getUser();

        boolean result = user.equals(author);
        if(result) {
            return true;
        }

        return false;
    }
}
