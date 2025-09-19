package kr.plusb3b.games.gamehub.domain.user.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.user.vo.business.MbNickName;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriBirth;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriEmail;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriGender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name="user_private")
public class UserPrivate {

    // PK
    @Id
    @Column(name = "pri_mb_id")
    private Long priMbId;

    // User와 1:1 매핑, 외래키 + PK 공유
    @OneToOne
    @JoinColumn(name = "mb_id")
    private User user;

    @Embedded
    @AttributeOverride(name = "priEmail",
            column = @Column(name = "pri_email", nullable = false))
    private PriEmail priEmail;


    @Embedded
    @AttributeOverride(name = "priBirth",
            column = @Column(name = "pri_birth", nullable = false))
    private PriBirth priBirth;


    @Embedded
    @AttributeOverride(name = "priGender",
            column = @Column(name = "pri_gender", nullable = false))
    private PriGender priGender;



    public UserPrivate(Long priMbId, User user, PriEmail priEmail, PriBirth priBirth, PriGender priGender) {
        this.priMbId = priMbId;
        this.user = user;
        this.priEmail = priEmail;
        this.priBirth = priBirth;
        this.priGender = priGender;
    }

    //여분컬럼 1~10
    /*
    private String pri_extra1;
    private String pri_extra2;
    private String pri_extra3;
    private String pri_extra4;
    private String pri_extra5;
    private String pri_extra6;
    private String pri_extra7;
    private String pri_extra8;
    private String pri_extra9;
    private String pri_extra10;
    */

    public UserPrivate() {}

    //이메일 변경
    public void changeEmail(PriEmail email) {
        this.priEmail = email;
    }

    //생년월일
    public void changeBirth(PriBirth priBirth) {
        this.priBirth = priBirth;
    }

    //성별 변경
    public void changeGender(PriGender gender) {
        this.priGender = gender;
    }

}
