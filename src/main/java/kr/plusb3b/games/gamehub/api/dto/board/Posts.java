package kr.plusb3b.games.gamehub.api.dto.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import kr.plusb3b.games.gamehub.repository.boardrepo.PostsRepository;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    @NotBlank(message = "제목을 입력하세요")
    private String postTitle;

    @NotBlank(message = "내용을 입력하세요.")
    private String postContent; //게시물 내용

    private int viewCount; //조회수
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int postAct;

    public Posts() {}

    //postAct의 값이 1인지 확인해주는 메소드
    public boolean isActivatePosts(){
        return this.postAct == 1;
    }
}

