package kr.plusb3b.games.gamehub.domain.board.vo;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class CreateNoticeVO {

    private final int viewCount;
    private final LocalDate updatedAt;
    private final int postAct;

    public CreateNoticeVO() {
        this.viewCount = 0;
        this.updatedAt = null;
        this.postAct = 1;
    }
}
