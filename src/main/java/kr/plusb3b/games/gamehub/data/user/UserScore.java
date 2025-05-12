package kr.plusb3b.games.gamehub.data.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name="user_score")
@Getter
@Setter
public class UserScore {

    //외래키
    @ManyToOne
    @JoinColumn(name="mb_id")
    private User user;

    //점수 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long score_id;

    //점수 종류 (ex. total, weekly, pvp)
    private String score_type;

    //최종 업데이트 시각
    private LocalDateTime updated_at;

    //여분컬럼
    /*
    private String score_extra1;
    private String score_extra2;
    private String score_extra3;
    private String score_extra4;
    private String score_extra5;
    private String score_extra6;
    private String score_extra7;
    private String score_extra8;
    private String score_extra9;
    private String score_extra10;
    */
    public UserScore(){}

}
