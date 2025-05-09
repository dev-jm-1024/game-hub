package kr.plusb3b.games.gamehub.data.board;

import java.time.LocalDateTime;

public class Posts {

    //외래키
    private Long board_id;

    //게시글 고유 아이디
    private Long post_id;

    //작성자: 외래키 (내부적으로 구분하는 mb_id 사용)
    private Long mb_id;

    //게시글 제목
    private String post_title;
    //조회수
    private int view_count;
    //생성날짜
    private LocalDateTime created_at;
    //업데이트 날짜
    private LocalDateTime updated_at;
    //게시글 활동여부
    private int post_act;

    //여분컬럼
    private String post_extra1;
    private String post_extra2;
    private String post_extra3;
    private String post_extra4;
    private String post_extra5;
    private String post_extra6;
    private String post_extra7;
    private String post_extra8;
    private String post_extra9;
    private String post_extra10;

    public Posts(){}

    public Long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Long board_id) {
        this.board_id = board_id;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public Long getMb_id() {
        return mb_id;
    }

    public void setMb_id(Long mb_id) {
        this.mb_id = mb_id;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public int getPost_act() {
        return post_act;
    }

    public void setPost_act(int post_act) {
        this.post_act = post_act;
    }

    public String getPost_extra1() {
        return post_extra1;
    }

    public void setPost_extra1(String post_extra1) {
        this.post_extra1 = post_extra1;
    }

    public String getPost_extra2() {
        return post_extra2;
    }

    public void setPost_extra2(String post_extra2) {
        this.post_extra2 = post_extra2;
    }

    public String getPost_extra3() {
        return post_extra3;
    }

    public void setPost_extra3(String post_extra3) {
        this.post_extra3 = post_extra3;
    }

    public String getPost_extra4() {
        return post_extra4;
    }

    public void setPost_extra4(String post_extra4) {
        this.post_extra4 = post_extra4;
    }

    public String getPost_extra5() {
        return post_extra5;
    }

    public void setPost_extra5(String post_extra5) {
        this.post_extra5 = post_extra5;
    }

    public String getPost_extra6() {
        return post_extra6;
    }

    public void setPost_extra6(String post_extra6) {
        this.post_extra6 = post_extra6;
    }

    public String getPost_extra7() {
        return post_extra7;
    }

    public void setPost_extra7(String post_extra7) {
        this.post_extra7 = post_extra7;
    }

    public String getPost_extra8() {
        return post_extra8;
    }

    public void setPost_extra8(String post_extra8) {
        this.post_extra8 = post_extra8;
    }

    public String getPost_extra9() {
        return post_extra9;
    }

    public void setPost_extra9(String post_extra9) {
        this.post_extra9 = post_extra9;
    }

    public String getPost_extra10() {
        return post_extra10;
    }

    public void setPost_extra10(String post_extra10) {
        this.post_extra10 = post_extra10;
    }
}
