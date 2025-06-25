package kr.plusb3b.games.gamehub.api.dto.board;

import jakarta.persistence.*;
import kr.plusb3b.games.gamehub.api.dto.game.Games;
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
    @Id // 해당 필드가 엔티티의 기본 키임을 명시합니다.
    @Column(name="board_id")
    private String boardId;

    // 게시판 이름 (예: 공지사항, 자유게시판 등)
    private String boardName;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Games> games = new ArrayList<>();

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

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }


    public List<Games> getGames() {
        return games;
    }

    public void setGames(List<Games> games) {
        this.games = games;
    }
}
