package kr.plusb3b.games.gamehub.api.dto.board;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "posts")
@Getter
@Setter
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long post_id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board; // 게시판 외래키

    @ManyToOne//여러개가 하나를 참조
    @JoinColumn(name = "mb_id")
    private User user; // 작성자 외래키

    private String post_title;
    private String post_content;
    private int view_count;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private int post_act;

    public Posts() {}
}

