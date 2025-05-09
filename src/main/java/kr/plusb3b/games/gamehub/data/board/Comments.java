package kr.plusb3b.games.gamehub.data.board;

public class Comments {

    //댓글 고유 아이디
    private Long comment_id;

    //어떤 게시글의 댓글인지? 외래키임
    private Long post_id;

    //작성자 아이디 (내부 식별 아이디. 로그인 아이디 아님)
    private Long mb_id;

    //댓글 내용
    private String comment_content;
    //좋아요 수
    private int like_count;
    //싫어요 수
    private int dislike_count;
    //신고횟수
    private int report_count;

    //여분컬럼
    private String comm_extra1;
    private String comm_extra2;
    private String comm_extra3;
    private String comm_extra4;
    private String comm_extra5;
    private String comm_extra6;
    private String comm_extra7;
    private String comm_extra8;
    private String comm_extra9;
    private String comm_extra10;

    public Comments() {}

    public Long getComment_id() {
        return comment_id;
    }

    public void setComment_id(Long comment_id) {
        this.comment_id = comment_id;
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

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getDislike_count() {
        return dislike_count;
    }

    public void setDislike_count(int dislike_count) {
        this.dislike_count = dislike_count;
    }

    public int getReport_count() {
        return report_count;
    }

    public void setReport_count(int report_count) {
        this.report_count = report_count;
    }

    public String getComm_extra1() {
        return comm_extra1;
    }

    public void setComm_extra1(String comm_extra1) {
        this.comm_extra1 = comm_extra1;
    }

    public String getComm_extra2() {
        return comm_extra2;
    }

    public void setComm_extra2(String comm_extra2) {
        this.comm_extra2 = comm_extra2;
    }

    public String getComm_extra3() {
        return comm_extra3;
    }

    public void setComm_extra3(String comm_extra3) {
        this.comm_extra3 = comm_extra3;
    }

    public String getComm_extra4() {
        return comm_extra4;
    }

    public void setComm_extra4(String comm_extra4) {
        this.comm_extra4 = comm_extra4;
    }

    public String getComm_extra5() {
        return comm_extra5;
    }

    public void setComm_extra5(String comm_extra5) {
        this.comm_extra5 = comm_extra5;
    }

    public String getComm_extra6() {
        return comm_extra6;
    }

    public void setComm_extra6(String comm_extra6) {
        this.comm_extra6 = comm_extra6;
    }

    public String getComm_extra7() {
        return comm_extra7;
    }

    public void setComm_extra7(String comm_extra7) {
        this.comm_extra7 = comm_extra7;
    }

    public String getComm_extra8() {
        return comm_extra8;
    }

    public void setComm_extra8(String comm_extra8) {
        this.comm_extra8 = comm_extra8;
    }

    public String getComm_extra9() {
        return comm_extra9;
    }

    public void setComm_extra9(String comm_extra9) {
        this.comm_extra9 = comm_extra9;
    }

    public String getComm_extra10() {
        return comm_extra10;
    }

    public void setComm_extra10(String comm_extra10) {
        this.comm_extra10 = comm_extra10;
    }
}
