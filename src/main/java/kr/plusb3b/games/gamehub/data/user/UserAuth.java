package kr.plusb3b.games.gamehub.data.user;

import java.time.LocalDateTime;

public class UserAuth {

    //외래키
    private Long mb_id;
    //고유키
    private Long auth_user_id;

    //사용자 비밀번호 추후 해시화 예정
    private String auth_password;

    //사용자 마지막 로그인 날짜
    private LocalDateTime auth_last_login;

    //여번컬럼
    private String auth_extra1;
    private String auth_extra2;
    private String auth_extra3;
    private String auth_extra4;
    private String auth_extra5;
    private String auth_extra6;
    private String auth_extra7;
    private String auth_extra8;
    private String auth_extra9;
    private String auth_extra10;

    public UserAuth(){}

    public Long getMb_id() {
        return mb_id;
    }

    public void setMb_id(Long mb_id) {
        this.mb_id = mb_id;
    }

    public Long getAuth_user_id() {
        return auth_user_id;
    }

    public void setAuth_user_id(Long auth_user_id) {
        this.auth_user_id = auth_user_id;
    }

    public String getAuth_password() {
        return auth_password;
    }

    public void setAuth_password(String auth_password) {
        this.auth_password = auth_password;
    }

    public LocalDateTime getAuth_last_login() {
        return auth_last_login;
    }

    public void setAuth_last_login(LocalDateTime auth_last_login) {
        this.auth_last_login = auth_last_login;
    }

    public String getAuth_extra1() {
        return auth_extra1;
    }

    public void setAuth_extra1(String auth_extra1) {
        this.auth_extra1 = auth_extra1;
    }

    public String getAuth_extra2() {
        return auth_extra2;
    }

    public void setAuth_extra2(String auth_extra2) {
        this.auth_extra2 = auth_extra2;
    }

    public String getAuth_extra3() {
        return auth_extra3;
    }

    public void setAuth_extra3(String auth_extra3) {
        this.auth_extra3 = auth_extra3;
    }

    public String getAuth_extra4() {
        return auth_extra4;
    }

    public void setAuth_extra4(String auth_extra4) {
        this.auth_extra4 = auth_extra4;
    }

    public String getAuth_extra5() {
        return auth_extra5;
    }

    public void setAuth_extra5(String auth_extra5) {
        this.auth_extra5 = auth_extra5;
    }

    public String getAuth_extra6() {
        return auth_extra6;
    }

    public void setAuth_extra6(String auth_extra6) {
        this.auth_extra6 = auth_extra6;
    }

    public String getAuth_extra7() {
        return auth_extra7;
    }

    public void setAuth_extra7(String auth_extra7) {
        this.auth_extra7 = auth_extra7;
    }

    public String getAuth_extra8() {
        return auth_extra8;
    }

    public void setAuth_extra8(String auth_extra8) {
        this.auth_extra8 = auth_extra8;
    }

    public String getAuth_extra9() {
        return auth_extra9;
    }

    public void setAuth_extra9(String auth_extra9) {
        this.auth_extra9 = auth_extra9;
    }

    public String getAuth_extra10() {
        return auth_extra10;
    }

    public void setAuth_extra10(String auth_extra10) {
        this.auth_extra10 = auth_extra10;
    }
}
