package kr.plusb3b.games.gamehub.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateBoardDto {

    @NotNull(message = "게시판의 이름이 공백이어선 안됩니다!")
    String boardName;


}
