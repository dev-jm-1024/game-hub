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
    private final PostsReactionCountRepository postsReactionCountRepo;
    private final UserPostsReactionRepository userPostsReactionRepo;

    public PostsInteractionServiceImpl(PostsRepository postsRepo,
                                       PostsReactionCountRepository postsReactionCountRepo,
                                       UserPostsReactionRepository userPostsReactionRepo) {
        this.postsRepo = postsRepo;
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
    public boolean likePost(User user, Posts posts) {
        try {
            // 1. 기존의 사용자 reaction 확인
            Optional<UserPostsReaction> reactOpt = userPostsReactionRepo.findByUserAndPosts(user, posts);

            if (reactOpt.isPresent()) {
                UserPostsReaction reaction = reactOpt.get();

                if (reaction.getReactionType() == UserPostsReaction.ReactionType.LIKE) {
                    // 케이스 1: 이미 좋아요 → 좋아요 취소
                    System.out.println("좋아요 취소 처리 시작");

                    userPostsReactionRepo.delete(reaction);
                    userPostsReactionRepo.flush(); // 즉시 DB 반영

                    int decrementResult = postsReactionCountRepo.decrementLikeCountByPostId(posts.getPostId());
                    System.out.println("좋아요 카운트 감소 결과: " + decrementResult);

                    return true;

                } else if (reaction.getReactionType() == UserPostsReaction.ReactionType.DISLIKE) {
                    // 케이스 2: 싫어요 → 좋아요로 변경
                    System.out.println("싫어요→좋아요 변경 처리 시작");

                    // 싫어요 카운트 감소
                    int decrementDislikeResult = postsReactionCountRepo.decrementDislikeCountByPostId(posts.getPostId());
                    System.out.println("싫어요 카운트 감소 결과: " + decrementDislikeResult);

                    // 반응 타입 변경
                    reaction.setReactionType(UserPostsReaction.ReactionType.LIKE);
                    userPostsReactionRepo.save(reaction);
                    userPostsReactionRepo.flush();

                    // 좋아요 카운트 증가
                    int incrementLikeResult = postsReactionCountRepo.incrementLikeCountByPostId(posts.getPostId());
                    System.out.println("좋아요 카운트 증가 결과: " + incrementLikeResult);

                    return true;
                }
            }

            // 케이스 3: 반응이 없는 경우 → 새로운 좋아요 등록
            System.out.println("새로운 좋아요 등록 처리 시작");

            // 좋아요 카운트 증가
            int incrementResult = postsReactionCountRepo.incrementLikeCountByPostId(posts.getPostId());
            System.out.println("좋아요 카운트 증가 결과: " + incrementResult);

            if (incrementResult == 0) {
                System.out.println("좋아요 카운트 증가 실패");
                return false;
            }

            // 새로운 반응 기록 생성
            SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
            Long reactionId = sf.nextId();

            UserPostsReaction newReaction = new UserPostsReaction(
                    reactionId,
                    user,
                    posts,
                    UserPostsReaction.ReactionType.LIKE,
                    LocalDate.now()
            );

            UserPostsReaction savedReaction = userPostsReactionRepo.save(newReaction);
            userPostsReactionRepo.flush();

            if (savedReaction == null) {
                System.out.println("사용자 반응 저장 실패");
                return false;
            }

            System.out.println("새로운 좋아요 등록 성공! ID: " + savedReaction.getReactionId());
            return true;

        } catch (Exception e) {
            System.out.println("좋아요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean dislikePost(User user, Posts posts) {
        try {
            // 1. 기존의 사용자 reaction 확인
            Optional<UserPostsReaction> reactOpt = userPostsReactionRepo.findByUserAndPosts(user, posts);

            if (reactOpt.isPresent()) {
                UserPostsReaction reaction = reactOpt.get();

                if (reaction.getReactionType() == UserPostsReaction.ReactionType.DISLIKE) {
                    // 케이스 1: 이미 싫어요 → 싫어요 취소
                    System.out.println("싫어요 취소 처리 시작");

                    userPostsReactionRepo.delete(reaction);
                    userPostsReactionRepo.flush();

                    int decrementResult = postsReactionCountRepo.decrementDislikeCountByPostId(posts.getPostId());
                    System.out.println("싫어요 카운트 감소 결과: " + decrementResult);

                    return true;

                } else if (reaction.getReactionType() == UserPostsReaction.ReactionType.LIKE) {
                    // 케이스 2: 좋아요 → 싫어요로 변경
                    System.out.println("좋아요→싫어요 변경 처리 시작");

                    // 좋아요 카운트 감소
                    int decrementLikeResult = postsReactionCountRepo.decrementLikeCountByPostId(posts.getPostId());
                    System.out.println("좋아요 카운트 감소 결과: " + decrementLikeResult);

                    // 반응 타입 변경
                    reaction.setReactionType(UserPostsReaction.ReactionType.DISLIKE);
                    userPostsReactionRepo.save(reaction);
                    userPostsReactionRepo.flush();

                    // 싫어요 카운트 증가
                    int incrementDislikeResult = postsReactionCountRepo.incrementDislikeCountByPostId(posts.getPostId());
                    System.out.println("싫어요 카운트 증가 결과: " + incrementDislikeResult);

                    return true;
                }
            }

            // 케이스 3: 반응이 없는 경우 → 새로운 싫어요 등록
            System.out.println("새로운 싫어요 등록 처리 시작");

            // 싫어요 카운트 증가
            int incrementResult = postsReactionCountRepo.incrementDislikeCountByPostId(posts.getPostId());
            System.out.println("싫어요 카운트 증가 결과: " + incrementResult);

            if (incrementResult == 0) {
                System.out.println("싫어요 카운트 증가 실패");
                return false;
            }

            // 새로운 반응 기록 생성
            SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
            Long reactionId = sf.nextId();

            UserPostsReaction newReaction = new UserPostsReaction(
                    reactionId,
                    user,
                    posts,
                    UserPostsReaction.ReactionType.DISLIKE,
                    LocalDate.now()
            );

            UserPostsReaction savedReaction = userPostsReactionRepo.save(newReaction);
            userPostsReactionRepo.flush();

            if (savedReaction == null) {
                System.out.println("사용자 반응 저장 실패");
                return false;
            }

            System.out.println("새로운 싫어요 등록 성공! ID: " + savedReaction.getReactionId());
            return true;

        } catch (Exception e) {
            System.out.println("싫어요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean likePostCancel(User user, Posts posts) {
        // likePost()에서 토글 처리하므로 동일하게 호출
        return likePost(user, posts);
    }

    @Override
    @Transactional
    public boolean dislikePostCancel(User user, Posts posts) {
        // dislikePost()에서 토글 처리하므로 동일하게 호출
        return dislikePost(user, posts);
    }
    @Override
    public boolean reportPost(User user, Posts posts) {
        return false;
    }

    @Override
    public boolean reportPostCancel(User user, Posts posts) {
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
