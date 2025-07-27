package kr.plusb3b.games.gamehub.domain.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "posts_reaction_count")
@Getter
@Setter
public class PostsReactionCount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reactionCountId;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts posts;

    private int likeCount;
    private int dislikeCount;
    private int reportCount;

    public PostsReactionCount() {}

    public PostsReactionCount(Posts posts, int likeCount,
                              int dislikeCount, int reportCount) {
        this.posts = posts;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.reportCount = reportCount;
    }

    // 좋아요 증가
    public void increaseLikeCount() {
        this.likeCount++;
    }

    // 좋아요 감소
    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    // 싫어요 증가
    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    // 싫어요 감소
    public void decreaseDislikeCount() {
        if (this.dislikeCount > 0) this.dislikeCount--;
    }

    // 신고 증가
    public void increaseReportCount() {
        this.reportCount++;
    }

    // 신고 감소
    public void decreaseReportCount() {
        if (this.reportCount > 0) this.reportCount--;
    }
}

