package kr.plusb3b.games.gamehub.api.dto.board;


import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.api.dto.user.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="comments")
public class Comments {

    //댓글 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    //어떤 게시글의 댓글인지? 외래키임
    @ManyToOne
    @JoinColumn(name = "postId")
    private Posts posts;

    //작성자 아이디 (내부 식별 아이디. 로그인 아이디 아님)
    @ManyToOne
    @JoinColumn(name="mbId")
    private User user;

    //댓글 내용
    private String commentContent;
    //좋아요 수
    private int likeCount;
    //싫어요 수
    private int dislikeCount;
    //신고횟수
    private int reportCount;

    //여분컬럼
    /*
    private String comm_extra1;
    private String comm_extra2;
    private String comm_extra3;
    private String comm_extra4;
    private String comm_extra5;
    private String comm_extra6;
    private String comm_extra7;
    private String comm_extra8;
    private String comm_extra9;
    private String comm_extra10;
     */

    public Comments() {}

}
