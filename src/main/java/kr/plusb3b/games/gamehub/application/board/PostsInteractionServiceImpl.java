package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import org.springframework.transaction.annotation.Transactional;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Service
public class PostsInteractionServiceImpl implements PostsInteractionService {

    private final PostsRepository postsRepo;
    private final AccessControlService access;
    private final PostsReactionCountRepository postsReactionCountRepo;
    private final UserPostsReactionRepository userPostsReactionRepo;

    public PostsInteractionServiceImpl(PostsRepository postsRepo, AccessControlService access,
                                       PostsReactionCountRepository postsReactionCountRepo,
                                       UserPostsReactionRepository userPostsReactionRepo) {
        this.postsRepo = postsRepo;
        this.access = access;
        this.postsReactionCountRepo = postsReactionCountRepo;
        this.userPostsReactionRepo = userPostsReactionRepo;
    }

    @Override
    //게시물 작성 시, PostsReactionCount 데이터 조립 및 삽입
    public boolean savePostsReactionCount(Long postId, PostsReactionCountVO prcVO) {

        Posts posts = postsRepo.findById(postId).orElse(null);
        if(posts == null) return false;

        postsReactionCountRepo.save(new PostsReactionCount(
           posts,prcVO.getLikeCount(), prcVO.getDislikeCount(),
                prcVO.getReportCount()
        ));


        return true;

    }

    @Override
    @Transactional
    public boolean likePost(Long postId, String authUserId, HttpServletRequest request) {

        // 1. 로그인된 사용자 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) return false;

        // 2. 인증된 사용자 ID 검증
        boolean checkAuthUserId = user.getUserAuth().getAuthUserId().equals(authUserId);
        if (!checkAuthUserId) return false;

        // 3. 게시글 존재 여부 확인
        Posts post = postsRepo.findById(postId).orElse(null);
        if (post == null) return false;

        // 4. 기존 반응 여부 확인
        Optional<UserPostsReaction> reactionOpt = userPostsReactionRepo.findByUserAndPost(user, post);

        if (reactionOpt.isEmpty()) {
            // 처음 좋아요 누름 → 저장 + 카운트 증가
            UserPostsReaction newReaction = new UserPostsReaction();
            newReaction.setUser(user);
            newReaction.setPost(post);
            newReaction.setReactionType(UserPostsReaction.ReactionType.LIKE);

            userPostsReactionRepo.save(newReaction);
            postsReactionCountRepo.incrementLikeCountByPostId(postId);

        } else {
            UserPostsReaction reaction = reactionOpt.get();

            if (reaction.getReactionType() == UserPostsReaction.ReactionType.LIKE) {
                // 좋아요 → 취소
                userPostsReactionRepo.delete(reaction);
                postsReactionCountRepo.decrementLikeCountByPostId(postId);

            } else {
                // 싫어요 → 좋아요로 전환
                reaction.setReactionType(UserPostsReaction.ReactionType.LIKE);
                postsReactionCountRepo.incrementLikeCountByPostId(postId);
                postsReactionCountRepo.decrementDislikeCountByPostId(postId); // 필요시 구현
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean likePostCancel(Long postId, String authUserId, HttpServletRequest request) {

        // 1. 로그인 사용자 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) return false;

        // 2. 로그인 ID 검증
        boolean checkAuthUserId = user.getUserAuth().getAuthUserId().equals(authUserId);
        if (!checkAuthUserId) return false;

        // 3. 게시글 존재 여부 확인
        Posts post = postsRepo.findById(postId).orElse(null);
        if (post == null) return false;

        // 4. 해당 유저가 좋아요 눌렀는지 확인
        Optional<UserPostsReaction> reactionOpt =
                userPostsReactionRepo.findByUserAndPost(user, post);

        if (reactionOpt.isPresent()) {
            UserPostsReaction reaction = reactionOpt.get();

            if (reaction.getReactionType() == UserPostsReaction.ReactionType.LIKE) {
                // 5. 좋아요 상태일 경우만 취소
                userPostsReactionRepo.delete(reaction);
                postsReactionCountRepo.decrementLikeCountByPostId(postId);
                return true;
            }
        }

        // 좋아요 기록이 없거나 이미 취소 상태면 실패
        return false;
    }


    @Override
    public boolean dislikePost(Long postId, String authUserId, HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean dislikePostCancel(Long postId, String authUserId, HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean reportPost(Long postId, String authUserId, HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean reportPostCancel(Long postId, String authUserId, HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean increaseViewCount(Long postId, HttpServletRequest request) {
        return false;
    }
}
