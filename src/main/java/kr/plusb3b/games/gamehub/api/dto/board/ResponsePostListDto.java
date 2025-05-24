package kr.plusb3b.games.gamehub.api.dto.board;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResponsePostListDto {

    private String boardId;
    private String boardName;

    private Long mbId;

    private Long postId;
    private String postTitle;
    private String postContent;
    private int viewCount;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public ResponsePostListDto() {

    }

    public ResponsePostListDto(String boardId, String boardName, Long mbId,
                               Long postId, String postTitle, String postContent,
                               int viewCount, LocalDate createdAt, LocalDate updatedAt) {
        this.boardId = boardId;
        this.boardName = boardName;
        this.mbId = mbId;
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
