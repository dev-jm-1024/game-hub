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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reactionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mb_id") // 사용자
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Posts post;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType; // LIKE, DISLIKE

    private LocalDate createdAt = LocalDate.now();

    public enum ReactionType {
        LIKE, DISLIKE, REPORT
    }
}
