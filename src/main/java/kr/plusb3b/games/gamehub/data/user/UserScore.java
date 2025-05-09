package kr.plusb3b.games.gamehub.data.user;

import java.time.LocalDateTime;

public class UserScore {

    //외래키
    private Long mb_id;

    //점수 고유 아이디
    private Long score_id;

    //점수 종류 (ex. total, weekly, pvp)
    private String score_type;

    //최종 업데이트 시각
    private LocalDateTime updated_at;

    //여분컬럼
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

    public UserScore(){}

    public Long getMb_id() {
        return mb_id;
    }

    public void setMb_id(Long mb_id) {
        this.mb_id = mb_id;
    }

    public Long getScore_id() {
        return score_id;
    }

    public void setScore_id(Long score_id) {
        this.score_id = score_id;
    }

    public String getScore_type() {
        return score_type;
    }

    public void setScore_type(String score_type) {
        this.score_type = score_type;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public String getScore_extra1() {
        return score_extra1;
    }

    public void setScore_extra1(String score_extra1) {
        this.score_extra1 = score_extra1;
    }

    public String getScore_extra2() {
        return score_extra2;
    }

    public void setScore_extra2(String score_extra2) {
        this.score_extra2 = score_extra2;
    }

    public String getScore_extra3() {
        return score_extra3;
    }

    public void setScore_extra3(String score_extra3) {
        this.score_extra3 = score_extra3;
    }

    public String getScore_extra4() {
        return score_extra4;
    }

    public void setScore_extra4(String score_extra4) {
        this.score_extra4 = score_extra4;
    }

    public String getScore_extra5() {
        return score_extra5;
    }

    public void setScore_extra5(String score_extra5) {
        this.score_extra5 = score_extra5;
    }

    public String getScore_extra6() {
        return score_extra6;
    }

    public void setScore_extra6(String score_extra6) {
        this.score_extra6 = score_extra6;
    }

    public String getScore_extra7() {
        return score_extra7;
    }

    public void setScore_extra7(String score_extra7) {
        this.score_extra7 = score_extra7;
    }

    public String getScore_extra8() {
        return score_extra8;
    }

    public void setScore_extra8(String score_extra8) {
        this.score_extra8 = score_extra8;
    }

    public String getScore_extra9() {
        return score_extra9;
    }

    public void setScore_extra9(String score_extra9) {
        this.score_extra9 = score_extra9;
    }

    public String getScore_extra10() {
        return score_extra10;
    }

    public void setScore_extra10(String score_extra10) {
        this.score_extra10 = score_extra10;
    }
}
