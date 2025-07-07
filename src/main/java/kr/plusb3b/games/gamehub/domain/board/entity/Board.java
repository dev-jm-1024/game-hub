package kr.plusb3b.games.gamehub.domain.board.entity;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.domain.game.entity.Games;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board") // 실제 DB의 "board" 테이블과 매핑되며, 대소문자 구분에 유의합니다.
@Getter
@Setter // Lombok을 사용해 getter/setter 메서드를 자동 생성합니다.
public class Board {

    // 기본 키(PK) 필드
    @Id // 해당 필드가 엔티티의 기본 키임을 명시.
    @Column(name="board_id")
    private String boardId;

    // 게시판 이름 (예: 공지사항, 자유게시판 등)
    private String boardName;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Games> games = new ArrayList<>();

    int boardAct; //게시판 활성화 컬럼

    // 확장용 필드: 현재는 사용하지 않으므로 주석 처리함
    /*
    private String board_extra1;
    private String board_extra2;
    private String board_extra3;
    private String board_extra4;
    private String board_extra5;
    private String board_extra6;
    private String board_extra7;
    private String board_extra8;
    private String board_extra9;
    private String board_extra10;
    */

    // 기본 생성자 (JPA에서 필수로 요구됨)
    public Board() {}

    //게시판 활성화
    public boolean activateBoard(){
        return boardAct == 1;
    }

    //게시판 비활성화
    public boolean deactivateBoard(){
        return boardAct == 0;
    }

    //게시판 상태가 활성화인 지 검사
    public boolean isActivateBoard(){
        if(this.boardAct == 1)
            return true;

        return false;
    }

    //게시판 이름 변경
    public void changeBoardName(String newName){
        this.boardName = newName;
    }

    //게시판 이름 업데이트
    public void updateBoardName(String newBoardName){
        if(newBoardName == null || newBoardName.isBlank()){
            throw new IllegalArgumentException("Board name cannot be blank");
        }

        this.boardName = newBoardName;
    }

}
