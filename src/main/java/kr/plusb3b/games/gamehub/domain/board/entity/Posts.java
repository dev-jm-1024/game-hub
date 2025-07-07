package kr.plusb3b.games.gamehub.domain.board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


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

    //Posts 객체
    public Posts( Board board, User user, String postTitle, String postContent,
                 int viewCount, LocalDate createdAt, LocalDate updatedAt, int postAct) {
        this.board = board;
        this.user = user;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postAct = postAct;
    }

    //글 제목 작성
    public void createTitle(String title){
        this.postTitle = title;
    }

    //글 제목 변경
    public void changeTitle(String newTitle){
        this.postTitle = newTitle;
    }

    //글 내용 작성
    public void createContent(String content){
        this.postContent = content;
    }

    //글 내용 변경
    public void changeContent(String newContent){
        this.postContent = newContent;
    }

    //조회수 증가
    public void increaseViewCount() {
        this.viewCount += 1;
    }

    //생성 및 수정 시간
    public void markCreated(){
        this.createdAt = LocalDate.now();
    }

    public void markUpdated(){
        this.updatedAt = LocalDate.now();
    }

    //비활성화 세팅
    public void changeDeactivatePost(){
        this.postAct = 0;
    }

    //postAct의 값이 1인지 확인해주는 메소드
    public boolean isActivatePosts(){
        return this.postAct == 1;
    }
}

