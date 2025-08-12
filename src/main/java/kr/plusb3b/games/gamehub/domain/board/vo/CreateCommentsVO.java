package kr.plusb3b.games.gamehub.domain.board.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateCommentsVO {

    private final int commentAct = 1;
    private final int dislikeCount = 0;
    private final int likeCount = 0;
    private final int reportCount = 0;

}
