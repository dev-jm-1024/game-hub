package kr.plusb3b.games.gamehub.data.game;

import java.time.LocalDateTime;

public class Games {

    //게임 고유 아이디
    private Long game_id;

    //게시물 아이디.
    //게임 테마별로 게시판 생성해서 거기에 보여질 예정
    //이를 통해 필터링
    private Long board_id;

    //게임 이름
    private String game_name;
    //게임소개
    private String game_description;
    //제작사 또는 팀 이름
    private String team_name;
    //권장사양
    private String specs;
    //게임 등록일
    private LocalDateTime created_at;
    //게임 노출 여부
    private int is_visible;
    //게임버전
    private String game_version;
    //게임장르
    private String genre;
    //게임 지원 플랫폼 (모바일 혹은 pc)
    private String platform;
    //게임 파일 무결성 확인용 해시값
    //중복 파일명 업로드 불가능
    private String game_hash;
    //관리자 승인여부
    private int is_approved;

    //여분컬럼
    private String game_extra1;
    private String game_extra2;
    private String game_extra3;
    private String game_extra4;
    private String game_extra5;
    private String game_extra6;
    private String game_extra7;
    private String game_extra8;
    private String game_extra9;
    private String game_extra10;

    public Games(){}

    public Long getGame_id() {
        return game_id;
    }

    public void setGame_id(Long game_id) {
        this.game_id = game_id;
    }

    public Long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Long board_id) {
        this.board_id = board_id;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_description() {
        return game_description;
    }

    public void setGame_description(String game_description) {
        this.game_description = game_description;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public int getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(int is_visible) {
        this.is_visible = is_visible;
    }

    public String getGame_version() {
        return game_version;
    }

    public void setGame_version(String game_version) {
        this.game_version = game_version;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGame_hash() {
        return game_hash;
    }

    public void setGame_hash(String game_hash) {
        this.game_hash = game_hash;
    }

    public int getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(int is_approved) {
        this.is_approved = is_approved;
    }

    public String getGame_extra1() {
        return game_extra1;
    }

    public void setGame_extra1(String game_extra1) {
        this.game_extra1 = game_extra1;
    }

    public String getGame_extra2() {
        return game_extra2;
    }

    public void setGame_extra2(String game_extra2) {
        this.game_extra2 = game_extra2;
    }

    public String getGame_extra3() {
        return game_extra3;
    }

    public void setGame_extra3(String game_extra3) {
        this.game_extra3 = game_extra3;
    }

    public String getGame_extra4() {
        return game_extra4;
    }

    public void setGame_extra4(String game_extra4) {
        this.game_extra4 = game_extra4;
    }

    public String getGame_extra5() {
        return game_extra5;
    }

    public void setGame_extra5(String game_extra5) {
        this.game_extra5 = game_extra5;
    }

    public String getGame_extra6() {
        return game_extra6;
    }

    public void setGame_extra6(String game_extra6) {
        this.game_extra6 = game_extra6;
    }

    public String getGame_extra7() {
        return game_extra7;
    }

    public void setGame_extra7(String game_extra7) {
        this.game_extra7 = game_extra7;
    }

    public String getGame_extra8() {
        return game_extra8;
    }

    public void setGame_extra8(String game_extra8) {
        this.game_extra8 = game_extra8;
    }

    public String getGame_extra9() {
        return game_extra9;
    }

    public void setGame_extra9(String game_extra9) {
        this.game_extra9 = game_extra9;
    }

    public String getGame_extra10() {
        return game_extra10;
    }

    public void setGame_extra10(String game_extra10) {
        this.game_extra10 = game_extra10;
    }
}
