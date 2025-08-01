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
}

