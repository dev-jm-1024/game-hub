package kr.plusb3b.games.gamehub.data.user;

import java.time.LocalDateTime;

public class UserLoginInfo {

    //외래키
    private Long mb_id;

    //로그인 기록 고유 아이디
    private Long login_info_id;

    //로그인 시각
    private LocalDateTime login_time;

    //접속 IP주소
    private String ip_address;

    //여분컬럼
    private String login_extra1;
    private String login_extra2;
    private String login_extra3;
    private String login_extra4;
    private String login_extra5;
    private String login_extra6;
    private String login_extra7;
    private String login_extra8;
    private String login_extra9;
    private String login_extra10;

    public UserLoginInfo() {}

    public Long getMb_id() {
        return mb_id;
    }

    public void setMb_id(Long mb_id) {
        this.mb_id = mb_id;
    }

    public Long getLogin_info_id() {
        return login_info_id;
    }

    public void setLogin_info_id(Long login_info_id) {
        this.login_info_id = login_info_id;
    }

    public LocalDateTime getLogin_time() {
        return login_time;
    }

    public void setLogin_time(LocalDateTime login_time) {
        this.login_time = login_time;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getLogin_extra1() {
        return login_extra1;
    }

    public void setLogin_extra1(String login_extra1) {
        this.login_extra1 = login_extra1;
    }

    public String getLogin_extra2() {
        return login_extra2;
    }

    public void setLogin_extra2(String login_extra2) {
        this.login_extra2 = login_extra2;
    }

    public String getLogin_extra3() {
        return login_extra3;
    }

    public void setLogin_extra3(String login_extra3) {
        this.login_extra3 = login_extra3;
    }

    public String getLogin_extra4() {
        return login_extra4;
    }

    public void setLogin_extra4(String login_extra4) {
        this.login_extra4 = login_extra4;
    }

    public String getLogin_extra5() {
        return login_extra5;
    }

    public void setLogin_extra5(String login_extra5) {
        this.login_extra5 = login_extra5;
    }

    public String getLogin_extra6() {
        return login_extra6;
    }

    public void setLogin_extra6(String login_extra6) {
        this.login_extra6 = login_extra6;
    }

    public String getLogin_extra7() {
        return login_extra7;
    }

    public void setLogin_extra7(String login_extra7) {
        this.login_extra7 = login_extra7;
    }

    public String getLogin_extra8() {
        return login_extra8;
    }

    public void setLogin_extra8(String login_extra8) {
        this.login_extra8 = login_extra8;
    }

    public String getLogin_extra9() {
        return login_extra9;
    }

    public void setLogin_extra9(String login_extra9) {
        this.login_extra9 = login_extra9;
    }

    public String getLogin_extra10() {
        return login_extra10;
    }

    public void setLogin_extra10(String login_extra10) {
        this.login_extra10 = login_extra10;
    }
}
