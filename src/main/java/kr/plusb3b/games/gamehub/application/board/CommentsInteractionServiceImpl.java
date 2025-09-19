package kr.plusb3b.games.gamehub.application.board;

import jakarta.transaction.Transactional;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import kr.plusb3b.games.gamehub.domain.board.entity.CommentsReactionCount;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsReactionCountRepository;
import kr.plusb3b.games.gamehub.domain.board.repository.CommentsRepository;
import kr.plusb3b.games.gamehub.domain.board.service.CommentsInteractionService;
import kr.plusb3b.games.gamehub.domain.board.vo.CommentsReactionCountVO;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserCommentsReaction;
import kr.plusb3b.games.gamehub.domain.user.repository.UserCommentsReactionRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CommentsInteractionServiceImpl implements CommentsInteractionService {

    private final CommentsRepository commentsRepo;
    private final CommentsReactionCountRepository commentsReactionRepo;
    private final UserCommentsReactionRepository userCommentsReactionRepo;
    private final UserRepository userRepo;

    public CommentsInteractionServiceImpl(CommentsRepository commentsRepo,
                                          CommentsReactionCountRepository commentsReactionRepo,
                                          UserCommentsReactionRepository userCommentsReactionRepo,
                                          UserRepository userRepo) {
        this.commentsRepo = commentsRepo;
        this.commentsReactionRepo = commentsReactionRepo;
        this.userCommentsReactionRepo = userCommentsReactionRepo;
        this.userRepo = userRepo;
    }

    @Override
    public boolean saveCommentsReactionCount(Long commentId, CommentsReactionCountVO crcVO) {
        Comments comment = commentsRepo.findById(commentId).orElse(null);
        if (comment == null) return false;

        commentsReactionRepo.save(new CommentsReactionCount(
                comment,
                crcVO.getLikeCount(),
                crcVO.getDislikeCount(),
                crcVO.getReportCount()
        ));

        return true;
    }

    @Override
    @Transactional
    public boolean likeComment(User user, Long commentId) {

        Comments comments = commentsRepo.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다")
        );

        try {
            // 1. 기존의 사용자 reaction 확인
            Optional<UserCommentsReaction> reactOpt = userCommentsReactionRepo.findByUserAndComment(user, comments);

            if (reactOpt.isPresent()) {
                UserCommentsReaction reaction = reactOpt.get();

                if (reaction.getReactionType() == UserCommentsReaction.ReactionType.LIKE) {
                    // 케이스 1: 이미 좋아요 → 좋아요 취소
                    System.out.println("댓글 좋아요 취소 처리 시작");

                    userCommentsReactionRepo.delete(reaction);
                    userCommentsReactionRepo.flush(); // 즉시 DB 반영

                    int decrementResult = commentsReactionRepo.decrementLikeCountByCommentId(commentId);
                    System.out.println("댓글 좋아요 카운트 감소 결과: " + decrementResult);

                    return true;

                } else if (reaction.getReactionType() == UserCommentsReaction.ReactionType.DISLIKE) {
                    // 케이스 2: 싫어요 → 좋아요로 변경
                    System.out.println("댓글 싫어요→좋아요 변경 처리 시작");

                    // 싫어요 카운트 감소
                    int decrementDislikeResult = commentsReactionRepo.decrementDislikeCountByCommentId(commentId);
                    System.out.println("댓글 싫어요 카운트 감소 결과: " + decrementDislikeResult);

                    // 반응 타입 변경
                    reaction.setReactionType(UserCommentsReaction.ReactionType.LIKE);
                    userCommentsReactionRepo.save(reaction);
                    userCommentsReactionRepo.flush();

                    // 좋아요 카운트 증가
                    int incrementLikeResult = commentsReactionRepo.incrementLikeCountByCommentId(commentId);
                    System.out.println("댓글 좋아요 카운트 증가 결과: " + incrementLikeResult);

                    return true;
                }
            }

            // 케이스 3: 반응이 없는 경우 → 새로운 좋아요 등록
            System.out.println("댓글 새로운 좋아요 등록 처리 시작");

            // 좋아요 카운트 증가
            int incrementResult = commentsReactionRepo.incrementLikeCountByCommentId(commentId);
            System.out.println("댓글 좋아요 카운트 증가 결과: " + incrementResult);

            if (incrementResult == 0) {
                System.out.println("댓글 좋아요 카운트 증가 실패 - ReactionCount 레코드가 없음. 새로 생성합니다.");

                // CommentsReactionCount 레코드가 없으면 새로 생성
                CommentsReactionCount newReactionCount = new CommentsReactionCount(
                        comments, 1, 0, 0  // 좋아요 1, 싫어요 0, 신고 0
                );
                commentsReactionRepo.save(newReactionCount);
                System.out.println("새로운 CommentsReactionCount 레코드 생성 완료");
            }

            // 새로운 반응 기록 생성
            SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
            Long reactionId = sf.nextId();

            UserCommentsReaction newReaction = new UserCommentsReaction(
                    reactionId,
                    user,
                    comments,
                    UserCommentsReaction.ReactionType.LIKE,
                    LocalDate.now()
            );

            UserCommentsReaction savedReaction = userCommentsReactionRepo.save(newReaction);
            userCommentsReactionRepo.flush();

            if (savedReaction == null) {
                System.out.println("댓글 사용자 반응 저장 실패");
                return false;
            }

            System.out.println("댓글 새로운 좋아요 등록 성공! ID: " + savedReaction.getReactionId());
            return true;

        } catch (Exception e) {
            System.out.println("댓글 좋아요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean dislikeComment(User user, Long commentId) {

        Comments comments = commentsRepo.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다")
        );

        try {
            // 1. 기존의 사용자 reaction 확인
            Optional<UserCommentsReaction> reactOpt = userCommentsReactionRepo.findByUserAndComment(user, comments);

            if (reactOpt.isPresent()) {
                UserCommentsReaction reaction = reactOpt.get();

                if (reaction.getReactionType() == UserCommentsReaction.ReactionType.DISLIKE) {
                    // 케이스 1: 이미 싫어요 → 싫어요 취소
                    System.out.println("댓글 싫어요 취소 처리 시작");

                    userCommentsReactionRepo.delete(reaction);
                    userCommentsReactionRepo.flush();

                    int decrementResult = commentsReactionRepo.decrementDislikeCountByCommentId(commentId);
                    System.out.println("댓글 싫어요 카운트 감소 결과: " + decrementResult);

                    return true;

                } else if (reaction.getReactionType() == UserCommentsReaction.ReactionType.LIKE) {
                    // 케이스 2: 좋아요 → 싫어요로 변경
                    System.out.println("댓글 좋아요→싫어요 변경 처리 시작");

                    // 좋아요 카운트 감소
                    int decrementLikeResult = commentsReactionRepo.decrementLikeCountByCommentId(commentId);
                    System.out.println("댓글 좋아요 카운트 감소 결과: " + decrementLikeResult);

                    // 반응 타입 변경
                    reaction.setReactionType(UserCommentsReaction.ReactionType.DISLIKE);
                    userCommentsReactionRepo.save(reaction);
                    userCommentsReactionRepo.flush();

                    // 싫어요 카운트 증가
                    int incrementDislikeResult = commentsReactionRepo.incrementDislikeCountByCommentId(commentId);
                    System.out.println("댓글 싫어요 카운트 증가 결과: " + incrementDislikeResult);

                    return true;
                }
            }

            // 케이스 3: 반응이 없는 경우 → 새로운 싫어요 등록
            System.out.println("댓글 새로운 싫어요 등록 처리 시작");

            // 싫어요 카운트 증가
            int incrementResult = commentsReactionRepo.incrementDislikeCountByCommentId(commentId);
            System.out.println("댓글 싫어요 카운트 증가 결과: " + incrementResult);

            if (incrementResult == 0) {
                System.out.println("댓글 싫어요 카운트 증가 실패 - ReactionCount 레코드가 없음. 새로 생성합니다.");

                // CommentsReactionCount 레코드가 없으면 새로 생성
                CommentsReactionCount newReactionCount = new CommentsReactionCount(
                        comments, 0, 1, 0  // 좋아요 0, 싫어요 1, 신고 0
                );
                commentsReactionRepo.save(newReactionCount);
                System.out.println("새로운 CommentsReactionCount 레코드 생성 완료");
            }

            // 새로운 반응 기록 생성
            SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
            Long reactionId = sf.nextId();

            UserCommentsReaction newReaction = new UserCommentsReaction(
                    reactionId,
                    user,
                    comments,
                    UserCommentsReaction.ReactionType.DISLIKE,
                    LocalDate.now()
            );

            UserCommentsReaction savedReaction = userCommentsReactionRepo.save(newReaction);
            userCommentsReactionRepo.flush();

            if (savedReaction == null) {
                System.out.println("댓글 사용자 반응 저장 실패");
                return false;
            }

            System.out.println("댓글 새로운 싫어요 등록 성공! ID: " + savedReaction.getReactionId());
            return true;

        } catch (Exception e) {
            System.out.println("댓글 싫어요 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean likeCommentCancel(User user, Long commentId) {
        // likeComment()에서 토글 처리하므로 동일하게 호출
        return likeComment(user, commentId);
    }

    @Override
    @Transactional
    public boolean dislikeCommentCancel(User user, Long commentId) {
        // dislikeComment()에서 토글 처리하므로 동일하게 호출
        return dislikeComment(user, commentId);
    }

    @Override
    @Transactional
    public boolean reportComment(User user, Long commentId) {

        Comments comments = commentsRepo.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다")
        );

        try {
            // 1. 자신의 댓글을 신고하는 것을 방지
            if (user.getMbId().equals(comments.getUser().getMbId())) {
                System.out.println("자신의 댓글은 신고할 수 없습니다.");
                return false;
            }

            // 2. 사용자가 해당 댓글을 이미 신고했는지 확인
            Optional<UserCommentsReaction> existingReaction = userCommentsReactionRepo.findByUserAndComment(user, comments);

            if (existingReaction.isPresent() &&
                    existingReaction.get().getReactionType() == UserCommentsReaction.ReactionType.REPORT) {
                System.out.println("이미 신고한 댓글입니다.");
                return false; // 이미 신고한 댓글인 경우 false 반환
            }

            // 3. 댓글의 신고 카운트 증가
            int result1 = commentsReactionRepo.incrementReportCountByCommentId(commentId);
            System.out.println("댓글 신고 카운트 증가 결과: " + result1);

            if (result1 == 0) {
                System.out.println("댓글 신고 카운트 증가 실패 - ReactionCount 레코드가 없음. 새로 생성합니다.");

                // CommentsReactionCount 레코드가 없으면 새로 생성
                CommentsReactionCount newReactionCount = new CommentsReactionCount(
                        comments, 0, 0, 1  // 좋아요 0, 싫어요 0, 신고 1
                );
                commentsReactionRepo.save(newReactionCount);
                System.out.println("새로운 CommentsReactionCount 레코드 생성 완료");
            }

            // 4. 댓글 작성자의 신고 받은 횟수 증가
            User commentAuthor = comments.getUser();
            commentAuthor.increaseReportCnt(); // User 엔티티의 메서드 사용
            userRepo.save(commentAuthor); // UserRepository 추가 필요
            userRepo.flush();

            System.out.println("댓글 작성자 신고 횟수 증가 완료");

            // 5. 사용자의 신고 기록 추가
            SnowflakeIdGenerator sf = new SnowflakeIdGenerator(0, 1);
            Long reactionId = sf.nextId();

            UserCommentsReaction reportReaction = new UserCommentsReaction(
                    reactionId,
                    user,
                    comments,
                    UserCommentsReaction.ReactionType.REPORT,
                    LocalDate.now()
            );

            UserCommentsReaction savedReaction = userCommentsReactionRepo.save(reportReaction);
            userCommentsReactionRepo.flush();

            if (savedReaction == null) {
                System.out.println("사용자 댓글 신고 기록 저장 실패");
                return false;
            }

            System.out.println("댓글 신고 처리 성공! 신고 기록 ID: " + savedReaction.getReactionId());
            return true;

        } catch (Exception e) {
            System.out.println("댓글 신고 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e; // @Transactional에 의해 롤백됨
        }
    }
    @Override
    @Transactional
    public boolean reportCommentCancel(User user, Comments comment) {
        return true;
    }

    @Override
    public CommentsReactionCount getCommentsReactionCount(Long commentId) {
        return commentsRepo.findById(commentId)
                .map(Comments::getReactionCount)
                .orElse(null);
    }
}