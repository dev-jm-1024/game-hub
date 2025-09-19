package kr.plusb3b.games.gamehub.domain.board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import kr.plusb3b.games.gamehub.domain.board.vo.business.BoardName;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostContent;
import kr.plusb3b.games.gamehub.domain.board.vo.business.PostTitle;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "posts")
@Getter
@Setter
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board; // 게시판 외래키

    @ManyToOne//여러개가 하나를 참조
    @JoinColumn(name = "mb_id")
    private User user; // 작성자 외래키

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "post_title", length = 255, nullable = false))
    private PostTitle postTitle;


    @Embedded
    @AttributeOverride(name = "postContent",
            column = @Column(name = "post_content", nullable = false))
    private PostContent postContent;

    private int viewCount; //조회수

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Column(name = "post_act")
    private int postAct;

    //게시물 최상단 부분에 올라갈 게시물 활성화 값
    private int importantAct;

    public Posts() {}

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFiles> postFiles = new ArrayList<>();

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();

    @OneToOne(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostsReactionCount reactionCount;

    public Posts(
            Board board, User user,
            PostTitle postTitle, PostContent postContent,
            int viewCount,
            LocalDate createdAt, LocalDate updatedAt,
            int postAct, int importantAct
    ){
        this.board = board;
        this.user = user;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postAct = postAct;
        this.importantAct = importantAct;
    }

    public Posts updatePosts(Posts posts) {
        if (posts.getPostTitle() != null) {
            this.changeTitle(posts.getPostTitle());
        }
        if (posts.getPostContent() != null) {
            this.changeContent(posts.getPostContent());
        }

        // 중요도 값 갱신
        this.importantAct = posts.getImportantAct();

        // postAct (활성/비활성 상태) 갱신
        this.postAct = posts.getPostAct();

        // updatedAt 자동 갱신
        this.updatedAt = LocalDate.now();

        return this;
    }


    //글 제목 작성
    public void createTitle(PostTitle postTitle){
        this.postTitle = postTitle;
    }

    //글 제목 변경
    public void changeTitle(PostTitle newTitle){
        this.postTitle = newTitle;
    }

    //글 내용 작성
    public void createContent(PostContent content){
        this.postContent = content;
    }

    //글 내용 변경
    public void changeContent(PostContent newContent){
        this.postContent = newContent;
    }


    //조회수 증가
    public void increaseViewCount() {
        this.viewCount += 1;
    }

    //비활성화 세팅
    public void changeDeactivatePost(){
        this.postAct = 0;
    }

    //postAct의 값이 1인지 확인해주는 메소드
    public boolean isActivatePosts(){
        return this.postAct == 1;
    }

    //importantAct 활성화
    public void changeActivateImportant(){
        this.importantAct = 1;
    }

    //importantAct 비활성화
    public void changeDeactivateImportant(){
        this.importantAct = 0;
    }

    //중요사항인가?
    public boolean isImportant(){
        return this.importantAct == 1;
    }


}

