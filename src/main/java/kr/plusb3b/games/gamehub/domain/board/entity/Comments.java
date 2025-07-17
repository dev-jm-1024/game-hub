package kr.plusb3b.games.gamehub.domain.board.entity;


import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

    //댓글 작성 날짜
    private LocalDate createdAt;

    //댓글 활성화 여부
    private int commentAct;

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

    public Comments(Posts posts, User user, String commentContent,
                    int likeCount, int dislikeCount, int reportCount,
                    LocalDate createdAt, int commentAct) {
        this.posts = posts;
        this.user = user;
        this.commentContent = commentContent;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.reportCount = reportCount;
        this.createdAt = createdAt;
        this.commentAct = commentAct;
    }

    //댓글이 활성화되어있는 지?
    public boolean isCommentsActivate(){
        return this.commentAct==1;
    }

    public void changeDeactivateComments(){
        this.commentAct=0;
    }

    public void changeActivateComments(){
        this.commentAct=1;
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        this.likeCount--;
    }

    public void increaseDislikeCount(){
        this.dislikeCount++;
    }

    public void decreaseDislikeCount(){
        this.dislikeCount--;
    }

    public void increaseReportCount(){
        this.reportCount++;
    }

    public void decreaseReportCount(){
        this.reportCount--;
    }
}
