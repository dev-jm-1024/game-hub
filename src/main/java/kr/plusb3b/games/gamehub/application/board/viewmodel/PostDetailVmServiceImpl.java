package kr.plusb3b.games.gamehub.application.board.viewmodel;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.domain.board.entity.*;
import kr.plusb3b.games.gamehub.domain.board.service.CommentService;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.service.PostFilesService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.service.PostsService;
import kr.plusb3b.games.gamehub.domain.board.service.viewmodel.PostDetailVmService;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.service.UserInteractionProvider;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.view.board.PostDetailVM;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostDetailVmServiceImpl implements PostDetailVmService {

    private final PostsService postsService;
    private final CommentService commentService;
    private final PostFilesService postFilesService;
    private final UserInteractionProvider userInteractionProvider;
    private final PostsInteractionService postsInteractionService;
    private final CommentsInteractionService commentsInteractionService;
    private final AccessControlService accessControlService;

    public PostDetailVmServiceImpl(PostsService postsService,
                                   CommentService commentService,
                                   PostFilesService postFilesService,
                                   UserInteractionProvider userInteractionProvider,
                                   PostsInteractionService postsInteractionService,
                                   CommentsInteractionService commentsInteractionService,
                                   AccessControlService accessControlService) {
        this.postsService = postsService;
        this.commentService = commentService;
        this.postFilesService = postFilesService;
        this.userInteractionProvider = userInteractionProvider;
        this.postsInteractionService = postsInteractionService;
        this.commentsInteractionService = commentsInteractionService;
        this.accessControlService = accessControlService;
    }

    @Override
    public PostDetailVM getPostDetailVm(String boardId, Long postId, HttpServletRequest request) {

        // 1. 게시물 정보 조회
        Posts posts = postsService.detailPosts(boardId, postId);
        if (posts == null) {
            throw new IllegalArgumentException("게시물을 찾을 수 없습니다.");
        }

        // 2. 첨부파일 정보 조회
        List<PostFiles> postFilesList = postFilesService.getPostFiles(postId);
        boolean hasPostFile = !postFilesList.isEmpty();

        // 3. 댓글 목록 조회
        List<Comments> commentsList = commentService.getComments(boardId, postId);

        // 4. 사용자 인증 정보 조회 (로그인하지 않은 경우도 처리)
        User user = getAuthenticatedUserSafely(request);

        // 5. 사용자별 데이터 조회 (로그인한 경우에만)
        boolean isAuthor = false;
        UserPostsReaction.ReactionType reactType = UserPostsReaction.ReactionType.NONE;
        boolean isUserReported = false;
        Map<Long, UserCommentsReaction.ReactionType> userCommentReactionMap = new HashMap<>();
        Map<Long, Boolean> userCommentReportMap = new HashMap<>();

        if (user != null) {
            isAuthor = postsService.isAuthor(request, postId);
            reactType = userInteractionProvider.getUserPostsReactionType(posts, user);
            isUserReported = userInteractionProvider.getUserPostsReportReactionType(posts, user);
            userCommentReactionMap = getUserCommentReactionMap(commentsList, user);
            userCommentReportMap = getUserCommentReportMap(commentsList, user);
        }

        // 6. 게시물 반응 카운트 조회 (모든 사용자에게 표시)
        PostsReactionCount postsReactionCount = postsInteractionService.getPostsReactionCount(postId);

        // 7. 댓글 반응 카운트 조회 (모든 사용자에게 표시)
        Map<Long, CommentsReactionCount> commentReactionMap = getCommentReactionMap(commentsList);

        return new PostDetailVM(
                posts,
                postFilesList,
                hasPostFile,
                commentsList,
                isAuthor,
                reactType,
                postsReactionCount,
                isUserReported,
                commentReactionMap,
                userCommentReactionMap,
                userCommentReportMap
        );
    }

    private User getAuthenticatedUserSafely(HttpServletRequest request) {
        try {
            return accessControlService.getAuthenticatedUser(request);
        } catch (AuthenticationCredentialsNotFoundException e) {
            // 로그인하지 않은 사용자 - null로 처리
            return null;
        }
    }

    private Map<Long, CommentsReactionCount> getCommentReactionMap(List<Comments> commentsList) {
        Map<Long, CommentsReactionCount> commentReactionMap = new HashMap<>();
        for (Comments comment : commentsList) {
            CommentsReactionCount crc = commentsInteractionService.getCommentsReactionCount(comment.getCommentId());
            commentReactionMap.put(comment.getCommentId(), crc);
        }
        return commentReactionMap;
    }

    private Map<Long, UserCommentsReaction.ReactionType> getUserCommentReactionMap(List<Comments> commentsList, User user) {
        Map<Long, UserCommentsReaction.ReactionType> userCommentReactionMap = new HashMap<>();
        for (Comments comment : commentsList) {
            UserCommentsReaction.ReactionType reactionType =
                    userInteractionProvider.getUserCommentsReactionType(comment, user);
            userCommentReactionMap.put(comment.getCommentId(), reactionType);
        }
        return userCommentReactionMap;
    }

    private Map<Long, Boolean> getUserCommentReportMap(List<Comments> commentsList, User user) {
        Map<Long, Boolean> userCommentReportMap = new HashMap<>();
        for (Comments comment : commentsList) {
            boolean isCommentReported =
                    userInteractionProvider.getUserCommentsReportReactionType(comment, user);
            userCommentReportMap.put(comment.getCommentId(), isCommentReported);
        }
        return userCommentReportMap;
    }
}