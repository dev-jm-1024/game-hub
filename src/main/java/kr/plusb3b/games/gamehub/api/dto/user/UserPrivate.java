package kr.plusb3b.games.gamehub.api.dto.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="user_private")
public class UserPrivate {

    // PK이자 FK인 필드
    @Id
    @Column(name = "pri_mb_id")
    private Long priMbId;

    // User와 1:1 매핑, 외래키 + PK 공유
    @OneToOne
    @JoinColumn(name = "mbId")
    private User user;

    //사용자 이메일
    private String priEmail;
    //사용자 생년월일
    private LocalDate priBirth;
    //사용자 성별
    private String priGender;

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
    public void changeEmail(String email) {
        this.priEmail = email;
    }

    //생년월일
    public void changeBirth(LocalDate birth) {
        this.priBirth = birth;
    }

    //성별 변경
    public void changeGender(String gender) {
        this.priGender = gender;
    }

}
