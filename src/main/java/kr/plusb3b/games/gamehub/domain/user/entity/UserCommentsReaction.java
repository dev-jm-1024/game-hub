package kr.plusb3b.games.gamehub.domain.user.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.board.entity.Comments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_comments_reaction")
@Getter
@Setter
public class UserCommentsReaction {

    @Id
    private Long reactionId;

    @ManyToOne
    @JoinColumn(name = "mb_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comments comments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    private LocalDate createdAt;

    public enum ReactionType {
        LIKE, DISLIKE, REPORT, NONE
    }

    public UserCommentsReaction() {}

    public UserCommentsReaction(Long reactionId, User user, Comments comments, ReactionType reactionType, LocalDate createdAt) {
        this.reactionId = reactionId;
        this.user = user;
        this.comments = comments;
        this.reactionType = reactionType;
        this.createdAt = createdAt;
    }

    // 생성자, getter/setter 생략 가능 (Lombok 사용 가능)
}
