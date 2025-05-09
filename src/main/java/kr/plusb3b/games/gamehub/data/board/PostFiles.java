package kr.plusb3b.games.gamehub.data.board;

import java.time.LocalDateTime;

public class PostFiles {

    //파일 고유 아이디
    private Long file_id;

    //파일이 속한 게시글 아이디 : 외래키
    private Long post_id;

    //파일경로 또는 이름
    private String file_url;

    //파일 유형
    private String file_type;

    //업로드 날짜
    private LocalDateTime upload_date;

    //여분컬럼1~10
    private String file_extra1;
    private String file_extra2;
    private String file_extra3;
    private String file_extra4;
    private String file_extra5;
    private String file_extra6;
    private String file_extra7;
    private String file_extra8;
    private String file_extra9;
    private String file_extra10;

    public PostFiles() {}

    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(Long file_id) {
        this.file_id = file_id;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public LocalDateTime getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(LocalDateTime upload_date) {
        this.upload_date = upload_date;
    }

    public String getFile_extra1() {
        return file_extra1;
    }

    public void setFile_extra1(String file_extra1) {
        this.file_extra1 = file_extra1;
    }

    public String getFile_extra2() {
        return file_extra2;
    }

    public void setFile_extra2(String file_extra2) {
        this.file_extra2 = file_extra2;
    }

    public String getFile_extra3() {
        return file_extra3;
    }

    public void setFile_extra3(String file_extra3) {
        this.file_extra3 = file_extra3;
    }

    public String getFile_extra4() {
        return file_extra4;
    }

    public void setFile_extra4(String file_extra4) {
        this.file_extra4 = file_extra4;
    }

    public String getFile_extra5() {
        return file_extra5;
    }

    public void setFile_extra5(String file_extra5) {
        this.file_extra5 = file_extra5;
    }

    public String getFile_extra6() {
        return file_extra6;
    }

    public void setFile_extra6(String file_extra6) {
        this.file_extra6 = file_extra6;
    }

    public String getFile_extra7() {
        return file_extra7;
    }

    public void setFile_extra7(String file_extra7) {
        this.file_extra7 = file_extra7;
    }

    public String getFile_extra8() {
        return file_extra8;
    }

    public void setFile_extra8(String file_extra8) {
        this.file_extra8 = file_extra8;
    }

    public String getFile_extra9() {
        return file_extra9;
    }

    public void setFile_extra9(String file_extra9) {
        this.file_extra9 = file_extra9;
    }

    public String getFile_extra10() {
        return file_extra10;
    }

    public void setFile_extra10(String file_extra10) {
        this.file_extra10 = file_extra10;
    }
}
