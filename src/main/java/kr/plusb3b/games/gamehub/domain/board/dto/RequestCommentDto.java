package kr.plusb3b.games.gamehub.domain.board.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCommentDto {

    private String commentContent;
    private String boardId;
    private Long postId;

    public RequestCommentDto() {}

    public RequestCommentDto(String commentContent, String boardId) {
        this.commentContent = commentContent;
        this.boardId = boardId;
    }
}
