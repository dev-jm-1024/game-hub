package kr.plusb3b.games.gamehub.api.dto.game;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.api.dto.board.Board;
import kr.plusb3b.games.gamehub.api.dto.user.User;
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
    private Long gaemId;

    //게시물 아이디.
    //게임 테마별로 게시판 생성해서 거기에 보여질 예정
    //이를 통해 필터링
    @ManyToOne
    @JoinColumn(name="boardId")
    private Board board;

    //올린 사람 아이디
    @ManyToOne
    @JoinColumn(name="mbId")
    private User user;

    //게임 이름
    private String gameName;
    //게임소개
    private String gameDescription;
    //제작사 또는 팀 이름
    private String teamName;
    //권장사양
    private String specs;
    //게임 등록일
    private LocalDateTime createdAt;
    //게임 노출 여부
    private int isVisible;
    //게임버전
    private String gameVersion;
    //게임장르
    private String genre;
    //게임 지원 플랫폼 (모바일 혹은 pc)
    private String platform;
    //게임 파일 무결성 확인용 해시값
    //중복 파일명 업로드 불가능
    private String gameHash;
    //관리자 승인여부
    private int isApproved;

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
