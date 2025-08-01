package kr.plusb3b.games.gamehub.domain.board.entity;


import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments_reaction_count")
@Getter
@Setter
public class CommentsReactionCount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comments comment;

    private int likeCount;
    private int dislikeCount;
    private int reportCount;

    public CommentsReactionCount() {}

    public CommentsReactionCount(Comments comment, int likeCount,
                                 int dislikeCount, int reportCount) {
        this.comment = comment;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.reportCount = reportCount;
    }
}
