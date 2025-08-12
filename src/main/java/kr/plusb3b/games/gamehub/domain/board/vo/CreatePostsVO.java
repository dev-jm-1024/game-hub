package kr.plusb3b.games.gamehub.domain.board.vo;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class CreatePostsVO {

    private final int viewCount;
    private final LocalDate updatedAt;
    private final int postAct;
    private final int importantAct;

    public CreatePostsVO() {
        this.viewCount = 0;
        this.updatedAt = null;
        this.postAct = 1;
        this.importantAct = 0;
    }

}
