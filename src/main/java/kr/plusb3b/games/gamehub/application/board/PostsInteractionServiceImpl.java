package kr.plusb3b.games.gamehub.application.board;

import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import kr.plusb3b.games.gamehub.domain.board.entity.PostsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.PostsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.PostsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.vo.PostsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPostsReaction;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.transaction.annotation.Transactional;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPostsReactionRepository;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
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

        // 1. 로그인 확인
        User user = access.getAuthenticatedUser(request);
        if (user == null) return false;

        // 2. 게시물 존재 확인
        Optional<Posts> postOpt = postsRepo.findById(postId);
        if (postOpt.isEmpty()) return false;
        Posts post = postOpt.get();

        // 3. 기존 반응 확인
        Optional<UserPostsReaction> reactOpt = userPostsReactionRepo.findByUserAndPosts(user, post);

        try {
            if (reactOpt.isPresent()) {
                UserPostsReaction reaction = reactOpt.get();

                if (reaction.getReactionType() == UserPostsReaction.ReactionType.LIKE) {
                    // 이미 좋아요 → 좋아요 취소
                    userPostsReactionRepo.delete(reaction);
                    postsReactionCountRepo.decrementLikeCountByPostId(postId);

                } else if (reaction.getReactionType() == UserPostsReaction.ReactionType.DISLIKE) {
                    // 싫어요 → 좋아요로 변경
                    postsReactionCountRepo.decrementDislikeCountByPostId(postId);
                    reaction.setReactionType(UserPostsReaction.ReactionType.LIKE);
                    userPostsReactionRepo.save(reaction); // 저장 추가
                    postsReactionCountRepo.incrementLikeCountByPostId(postId);
                }

            } else {
                // 처음 좋아요
                SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 0);
                Long reactionId = sf.nextId();

                UserPostsReaction newReaction = new UserPostsReaction(
                        reactionId,
                        user,
                        post,
                        UserPostsReaction.ReactionType.LIKE,
                        LocalDate.now()
                );

                UserPostsReaction saved = userPostsReactionRepo.save(newReaction);
                userPostsReactionRepo.flush(); // 강제로 DB에 즉시 반영
                System.out.println("저장 성공! ID: " + saved.getReactionId());

                postsReactionCountRepo.incrementLikeCountByPostId(postId);
            }
        } catch (Exception e) {
            System.out.println("좋아요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e; // 트랜잭션 롤백
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
                userPostsReactionRepo.findByUserAndPosts(user, post);

        // 해당 사용자가 interaction 기록 있는지 확인하기
        if (reactionOpt.isPresent()) { // 기존에 사용자가 interaction가 있는 경우
            UserPostsReaction reaction = reactionOpt.get();

            // 좋아요 기록이 있으면 삭제해야함
            if (reaction.getReactionType().equals(UserPostsReaction.ReactionType.LIKE)) {
                try {
                    userPostsReactionRepo.delete(reaction);
                    postsReactionCountRepo.decrementLikeCountByPostId(postId);
                    return true; // 좋아요 취소 성공
                } catch (Exception e) {
                    System.out.println("좋아요 취소 실패: " + e.getMessage());
                    e.printStackTrace();
                    throw e; // 트랜잭션 롤백
                }
            }
            // 싫어요나 다른 반응이 있는 경우는 좋아요 취소 대상이 아니므로 false 반환
        }
        // 반응 기록이 없거나 좋아요가 아닌 경우
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

    @Override
    public PostsReactionCount getPostsReactionCount(Long postId) {
        return postsRepo.findById(postId)
                .map(Posts::getReactionCount)
                .orElse(null);
    }
}
