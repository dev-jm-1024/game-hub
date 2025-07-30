package kr.plusb3b.games.gamehub.domain.user.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Posts;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_posts_reaction")
@Getter
@Setter
public class UserPostsReaction {

    @Id
    private Long reactionId;

    @ManyToOne
    @JoinColumn(name = "mb_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts posts;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    // 기본 생성자
    public UserPostsReaction() {}

    public UserPostsReaction(Long reactionId,User user, Posts posts,
                             ReactionType reactionType, LocalDate createdAt) {
        this.reactionId = reactionId;
        this.user = user;
        this.posts = posts;
        this.reactionType = reactionType;
        this.createdAt = createdAt;
    }

    public enum ReactionType {
        LIKE, DISLIKE, REPORT, NONE
    }
}