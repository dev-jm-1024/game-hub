package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.dto.RequestCommentDto;
import kr.plusb3b.games.gamehub.domain.board.entity.Board;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.repository.BoardRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.vo.CreateCommentsVO;
import kr.plusb3b.games.gamehub.domain.board.vo.business.CommentContent;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepo;
    private final PostsRepository postsRepo;
    private final BoardRepository boardRepo;

    public CommentServiceImpl(CommentsRepository commentsRepo, PostsRepository postsRepo,
                              BoardRepository boardRepo) {
        this.commentsRepo = commentsRepo;
        this.postsRepo = postsRepo;
        this.boardRepo = boardRepo;
    }

    @Override
    public Comments createComment(RequestCommentDto requestCommentDto, User user) {

        // 1. 게시글 조회
        Posts post = postsRepo.findById(requestCommentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        CreateCommentsVO cvo = new CreateCommentsVO();

        // 2. 댓글 엔티티 생성 ,저장 및 반환
        return commentsRepo.save(new Comments(
                post,
                user,
                CommentContent.of(requestCommentDto.getCommentContent()),
                cvo.getLikeCount(),
                cvo.getDislikeCount(),
                cvo.getReportCount(),
                LocalDate.now(),
                cvo.getCommentAct()
        ));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Comments> getComments(String boardId, Long postId) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시판이 존재하지 않습니다"));

        Posts posts = postsRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다"));

        // 게시판 불일치 예외
        if (!posts.getBoard().getBoardId().equals(boardId)) {
            throw new IllegalArgumentException("게시글이 해당 게시판에 존재하지 않습니다");
        }

        return commentsRepo.findByPosts_PostIdAndCommentActOrderByCreatedAtDesc(postId, 1);
    }

    @Override
    @Transactional
    public boolean updateCommentContents(Long commentId, String commentContent) {

        Comments comments = commentsRepo.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 내용이 같은 경우엔 아무것도 안 함
        if (comments.getCommentContent().equals(commentContent)) {
            return false;
        }
        comments.changeCommentContent(CommentContent.of(commentContent));

        return true;
    }

}
