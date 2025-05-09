package kr.plusb3b.games.gamehub.data.user;

import java.time.LocalDateTime;
//@Entity
//@Table(name = "user")
public class User {

    private Long mb_id;
    private String mb_nickname;
    private String mb_profile_url;
    private String mb_status_message;
    private LocalDateTime mb_join_date;
    private int mb_act;
    private int mb_report_cnt;

    //여분컬럼1~10
    private String mb_extra1;
    private String mb_extra2;
    private String mb_extra3;
    private String mb_extra4;
    private String mb_extra5;
    private String mb_extra6;
    private String mb_extra7;
    private String mb_extra8;
    private String mb_extra9;
    private String mb_extra10;

    public User () {}

    public Long getMb_id() {
        return mb_id;
    }

    public void setMb_id(Long mb_id) {
        this.mb_id = mb_id;
    }

    public String getMb_nickname() {
        return mb_nickname;
    }

    public void setMb_nickname(String mb_nickname) {
        this.mb_nickname = mb_nickname;
    }

    public String getMb_profile_url() {
        return mb_profile_url;
    }

    public void setMb_profile_url(String mb_profile_url) {
        this.mb_profile_url = mb_profile_url;
    }

    public String getMb_status_message() {
        return mb_status_message;
    }

    public void setMb_status_message(String mb_status_message) {
        this.mb_status_message = mb_status_message;
    }

    public LocalDateTime getMb_join_date() {
        return mb_join_date;
    }

    public void setMb_join_date(LocalDateTime mb_join_date) {
        this.mb_join_date = mb_join_date;
    }

    public int getMb_act() {
        return mb_act;
    }

    public void setMb_act(int mb_act) {
        this.mb_act = mb_act;
    }

    public int getMb_report_cnt() {
        return mb_report_cnt;
    }

    public void setMb_report_cnt(int mb_report_cnt) {
        this.mb_report_cnt = mb_report_cnt;
    }



    public String getMb_extra1() {
        return mb_extra1;
    }

    public void setMb_extra1(String mb_extra1) {
        this.mb_extra1 = mb_extra1;
    }

    public String getMb_extra2() {
        return mb_extra2;
    }

    public void setMb_extra2(String mb_extra2) {
        this.mb_extra2 = mb_extra2;
    }

    public String getMb_extra3() {
        return mb_extra3;
    }

    public void setMb_extra3(String mb_extra3) {
        this.mb_extra3 = mb_extra3;
    }

    public String getMb_extra4() {
        return mb_extra4;
    }

    public void setMb_extra4(String mb_extra4) {
        this.mb_extra4 = mb_extra4;
    }

    public String getMb_extra5() {
        return mb_extra5;
    }

    public void setMb_extra5(String mb_extra5) {
        this.mb_extra5 = mb_extra5;
    }

    public String getMb_extra6() {
        return mb_extra6;
    }

    public void setMb_extra6(String mb_extra6) {
        this.mb_extra6 = mb_extra6;
    }

    public String getMb_extra7() {
        return mb_extra7;
    }

    public void setMb_extra7(String mb_extra7) {
        this.mb_extra7 = mb_extra7;
    }

    public String getMb_extra8() {
        return mb_extra8;
    }

    public void setMb_extra8(String mb_extra8) {
        this.mb_extra8 = mb_extra8;
    }

    public String getMb_extra9() {
        return mb_extra9;
    }

    public void setMb_extra9(String mb_extra9) {
        this.mb_extra9 = mb_extra9;
    }

    public String getMb_extra10() {
        return mb_extra10;
    }

    public void setMb_extra10(String mb_extra10) {
        this.mb_extra10 = mb_extra10;
    }
}
