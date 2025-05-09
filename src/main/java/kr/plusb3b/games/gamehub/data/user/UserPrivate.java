package kr.plusb3b.games.gamehub.data.user;

import java.time.LocalDateTime;

public class UserPrivate {

    //외래키
    private Long mb_id;

    //사용자 이메일
    private String pri_email;
    //사용자 생년월일
    private LocalDateTime pri_birth;
    //사용자 성별
    private String pri_gender;

    //여분컬럼 1~10
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

    public UserPrivate() {}

    public Long getMb_id() {
        return mb_id;
    }

    public void setMb_id(Long mb_id) {
        this.mb_id = mb_id;
    }

    public String getPri_email() {
        return pri_email;
    }

    public void setPri_email(String pri_email) {
        this.pri_email = pri_email;
    }

    public LocalDateTime getPri_birth() {
        return pri_birth;
    }

    public void setPri_birth(LocalDateTime pri_birth) {
        this.pri_birth = pri_birth;
    }

    public String getPri_gender() {
        return pri_gender;
    }

    public void setPri_gender(String pri_gender) {
        this.pri_gender = pri_gender;
    }

    public String getPri_extra1() {
        return pri_extra1;
    }

    public void setPri_extra1(String pri_extra1) {
        this.pri_extra1 = pri_extra1;
    }

    public String getPri_extra2() {
        return pri_extra2;
    }

    public void setPri_extra2(String pri_extra2) {
        this.pri_extra2 = pri_extra2;
    }

    public String getPri_extra3() {
        return pri_extra3;
    }

    public void setPri_extra3(String pri_extra3) {
        this.pri_extra3 = pri_extra3;
    }

    public String getPri_extra4() {
        return pri_extra4;
    }

    public void setPri_extra4(String pri_extra4) {
        this.pri_extra4 = pri_extra4;
    }

    public String getPri_extra5() {
        return pri_extra5;
    }

    public void setPri_extra5(String pri_extra5) {
        this.pri_extra5 = pri_extra5;
    }

    public String getPri_extra6() {
        return pri_extra6;
    }

    public void setPri_extra6(String pri_extra6) {
        this.pri_extra6 = pri_extra6;
    }

    public String getPri_extra7() {
        return pri_extra7;
    }

    public void setPri_extra7(String pri_extra7) {
        this.pri_extra7 = pri_extra7;
    }

    public String getPri_extra8() {
        return pri_extra8;
    }

    public void setPri_extra8(String pri_extra8) {
        this.pri_extra8 = pri_extra8;
    }

    public String getPri_extra9() {
        return pri_extra9;
    }

    public void setPri_extra9(String pri_extra9) {
        this.pri_extra9 = pri_extra9;
    }

    public String getPri_extra10() {
        return pri_extra10;
    }

    public void setPri_extra10(String pri_extra10) {
        this.pri_extra10 = pri_extra10;
    }
}
