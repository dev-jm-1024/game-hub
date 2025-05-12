package kr.plusb3b.games.gamehub.data.game;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.data.board.Board;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "games")
public class Games {

    //게임 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long game_id;

    //게시물 아이디.
    //게임 테마별로 게시판 생성해서 거기에 보여질 예정
    //이를 통해 필터링
    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;

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
    /*
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
    */
    public Games(){}

}
