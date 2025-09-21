package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionHelper;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserInteractionProvider;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Service
public class PostsInteractionServiceImpl implements PostsInteractionService {

    private final PostsRepository postsRepo;
    private final UserPostsReactionRepository userPostsReactionRepo;
    private final UserInteractionProvider userInteractionProvider;
    private final UserRepository userRepo;
    private final PostsInteractionHelper postsInteractionHelper;

    public PostsInteractionServiceImpl(PostsRepository postsRepo,
                                       UserPostsReactionRepository userPostsReactionRepo,
                                       UserInteractionProvider userInteractionProvider,
                                       UserRepository userRepo,
                                       PostsInteractionHelper postsInteractionHelper) {
        this.postsRepo = postsRepo;
        this.userPostsReactionRepo = userPostsReactionRepo;
        this.userInteractionProvider = userInteractionProvider;
        this.userRepo = userRepo;
        this.postsInteractionHelper = postsInteractionHelper;
    }

    @Override
    public boolean savePostsReactionCount(Long postId, PostsReactionCountVO prcVO) {
        try {
            Posts posts = postsRepo.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다")
            );

            postsInteractionHelper.createInitialReactionCount(posts, prcVO);
            return true;
        } catch (Exception e) {
            System.out.println("게시물 반응 카운트 저장 실패: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean likePost(User user, Long postId) {

        try {

            // 게시물 존재 확인
            Optional<Posts> postsOpt = postsRepo.findById(postId);
            if (postsOpt.isEmpty()) {
                return false;
            }

            Optional<UserPostsReaction> reactOpt = userPostsReactionRepo.findByUserAndPosts(user, postsOpt.get());

            if (reactOpt.isEmpty()) {
                // 케이스 1: 처음 좋아요 누르기
                return handleNewLikeReaction(user,  postsOpt.get());
            }

            UserPostsReaction reaction = reactOpt.get();
            return handleExistingReactionForLike(reaction);

        } catch (Exception e) {
            System.out.println("좋아요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean dislikePost(User user, Long postId) {
        try {

            // 게시물 존재 확인
            Optional<Posts> postsOpt = postsRepo.findById(postId);
            if (postsOpt.isEmpty()) return false;

            Optional<UserPostsReaction> reactOpt = userPostsReactionRepo.findByUserAndPosts(user, postsOpt.get());

            if (reactOpt.isEmpty()) {
                // 케이스 1: 처음 싫어요 누르기
                return handleNewDislikeReaction(user, postsOpt.get());
            }

            UserPostsReaction reaction = reactOpt.get();
            return handleExistingReactionForDislike(reaction);

        } catch (Exception e) {
            System.out.println("싫어요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean reportPost(User user, Long postId) {
        try {

            // 게시물 존재 확인
            Optional<Posts> postsOpt = postsRepo.findById(postId);
            if (postsOpt.isEmpty()) return false;


            // 1. 자신의 게시물 신고 방지
            if (user.getMbId().equals(postsOpt.get().getUser().getMbId())) {
                return false;
            }

            // 2. 중복 신고 확인
            if (userInteractionProvider.getUserPostsReportReactionType(postsOpt.get(), user)) {
                return false;
            }

            // 3. 신고 처리
            return handleReportReaction(user, postsOpt.get());

        } catch (Exception e) {
            System.out.println("신고 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean likePostCancel(User user, Long postId) {
        return likePost(user, postId);
    }

    @Override
    @Transactional
    public boolean dislikePostCancel(User user, Long postId) {
        return dislikePost(user, postId);
    }

    @Override
    public boolean reportPostCancel(User user, Posts posts) {
        // TODO: 신고 취소 기능 구현 필요
        return false;
    }

    @Override
    public boolean increaseViewCount(Long postId, HttpServletRequest request) {
        // TODO: 조회수 증가 기능 구현 필요
        return false;
    }

    @Override
    public PostsReactionCount getPostsReactionCount(Long postId) {
        return postsRepo.findById(postId)
                .map(Posts::getReactionCount)
                .orElse(null);
    }

    // === 헬퍼 메소드들 ===

    //사용자의 리액션이 없는 경우 진행해주는 메소드 - 좋아요 ver
    private boolean handleNewLikeReaction(User user, Posts posts) {
        UserPostsReaction newReaction = postsInteractionHelper.recordReaction(
                user, posts, UserPostsReaction.ReactionType.LIKE);
        postsInteractionHelper.increaseReactionCount(newReaction);
        return true;
    }

    //사용자의 리액션이 없는 경우 진행해주는 메소드 - 싫어요 ver
    private boolean handleNewDislikeReaction(User user, Posts posts) {
        UserPostsReaction newReaction = postsInteractionHelper.recordReaction(
                user, posts, UserPostsReaction.ReactionType.DISLIKE);
        postsInteractionHelper.increaseReactionCount(newReaction);
        return true;
    }

    //사용자의 리액션이 "있는" 경우 진행해주는 메소드 - 좋아요 ver
    private boolean handleExistingReactionForLike(UserPostsReaction reaction) {
        switch (reaction.getReactionType()) {
            case LIKE:
                // 좋아요 취소
                postsInteractionHelper.decreaseReactionCount(reaction);
                postsInteractionHelper.cancelUserReaction(reaction);
                break;

            case DISLIKE:
                // 싫어요 → 좋아요 변경
                postsInteractionHelper.decreaseReactionCount(reaction);
                postsInteractionHelper.changeReaction(reaction);

                // 메모리 객체 동기화
                reaction.setReactionType(UserPostsReaction.ReactionType.LIKE);
                postsInteractionHelper.increaseReactionCount(reaction);
                break;

            default:
                return false;
        }
        return true;
    }

    private boolean handleExistingReactionForDislike(UserPostsReaction reaction) {
        switch (reaction.getReactionType()) {
            case DISLIKE:
                // 싫어요 취소
                postsInteractionHelper.decreaseReactionCount(reaction);
                postsInteractionHelper.cancelUserReaction(reaction);
                break;

            case LIKE:
                // 좋아요 → 싫어요 변경
                postsInteractionHelper.decreaseReactionCount(reaction);
                postsInteractionHelper.changeReaction(reaction);

                // 메모리 객체 동기화
                reaction.setReactionType(UserPostsReaction.ReactionType.DISLIKE);
                postsInteractionHelper.increaseReactionCount(reaction);
                break;

            default:
                return false;
        }
        return true;
    }

    private boolean handleReportReaction(User user, Posts posts) {
        // 신고 카운트 증가
        UserPostsReaction reportReaction = postsInteractionHelper.recordReaction(
                user, posts, UserPostsReaction.ReactionType.REPORT);
        postsInteractionHelper.increaseReactionCount(reportReaction);

        // 게시물 작성자의 신고 받은 횟수 증가
        User postAuthor = posts.getUser();
        postAuthor.increaseReportCnt();
        userRepo.save(postAuthor);
        userRepo.flush();

        return true;
    }


}