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
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board; // 게시판 외래키

    @ManyToOne//여러개가 하나를 참조
    @JoinColumn(name = "mbId")
    private User user; // 작성자 외래키

    private String postTitle;
    private String postContent; //게시물 내용
    private int viewCount; //조회수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int postAct;

    public Posts() {}
}

