package kr.plusb3b.games.gamehub.domain.board.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class BoardName {

    private String boardName;

    public BoardName(){

    }

    private BoardName(String boardName){
        this.boardName = boardName;
    }

    // ✅ static 메서드로 변경
    public static BoardName of(String boardName) {
        if(boardName == null || boardName.isEmpty()){
            throw new IllegalArgumentException("게시판 이름은 공백이어서는 안됩니다");
        } else if (boardName.length() > 20) {
            throw new IllegalArgumentException("게시판 이름은 20글자가 넘어가서는 안됩니다");
        }

        return new BoardName(boardName.trim()); // trim 추가
    }

    @Override
    public String toString() {
        return boardName;
    }
}
